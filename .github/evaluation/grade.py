
import argparse
import json
import os
import pathlib
import sys
import xml.etree.ElementTree as ET

STATUS_OK = "✅"
STATUS_PARTIAL = "🟡"
STATUS_FAIL = "❌"


def parse_args():
    parser = argparse.ArgumentParser()
    parser.add_argument("--spec", default=".github/evaluation/requirements.json")
    parser.add_argument("--reports", default="target/surefire-reports")
    parser.add_argument("--checkstyle-status", type=int, default=0)
    parser.add_argument("--build-status", type=int, default=0)
    parser.add_argument("--tampered", default="false")
    parser.add_argument("--out", default="nota.md")
    return parser.parse_args()


def read_reports(reports_dir):
    """Lê os XMLs do surefire e devolve {classe: [(nome, passou, detalhe)]}."""
    results = {}
    path = pathlib.Path(reports_dir)
    if not path.is_dir():
        return results

    for xml_file in sorted(path.glob("TEST-*.xml")):
        try:
            root = ET.parse(xml_file).getroot()
        except ET.ParseError:
            continue
        for case in root.iter("testcase"):
            class_name = case.get("classname") or root.get("name") or ""
            name = case.get("name") or "(sem nome)"
            failure = case.find("failure")
            error = case.find("error")
            skipped = case.find("skipped")
            if skipped is not None:
                continue
            problem = failure if failure is not None else error
            passed = problem is None
            detail = ""
            if problem is not None:
                detail = (problem.get("message") or problem.get("type") or "").strip()
                detail = " ".join(detail.split())[:180]
            results.setdefault(class_name, []).append((name, passed, detail))
    return results


def evaluate(spec, results, checkstyle_ok, build_ok):
    rows = []
    total = 0.0
    for requirement in spec["requisitos"]:
        weight = float(requirement["peso"])

        if requirement.get("tipo") == "lint":
            ratio = 1.0 if (checkstyle_ok and build_ok) else 0.0
            detail = "sem violações" if ratio else "há violações de estilo ou o build falhou"
            cases = []
        else:
            cases = results.get(requirement["classe"], [])
            if not build_ok:
                ratio = 0.0
                detail = "não executado (o projeto não compilou)"
            elif not cases:
                ratio = 0.0
                detail = "nenhum teste executado"
            else:
                passed = sum(1 for _, ok, _ in cases if ok)
                ratio = passed / len(cases)
                detail = f"{passed}/{len(cases)} testes"

        if spec.get("modo") == "tudo-ou-nada" and ratio < 1.0:
            ratio = 0.0

        score = weight * ratio
        total += score
        rows.append(
            {
                "id": requirement["id"],
                "titulo": requirement["titulo"],
                "peso": weight,
                "ratio": ratio,
                "nota": score,
                "detalhe": detail,
                "casos": cases,
            }
        )
    return rows, round(total, 1)


def status_icon(ratio):
    if ratio >= 1.0:
        return STATUS_OK
    if ratio > 0.0:
        return STATUS_PARTIAL
    return STATUS_FAIL


def render(spec, rows, total, tampered):
    minimum = float(spec.get("notaMinima", 0))
    verdict = "APROVADO" if total >= minimum else "REPROVADO"
    lines = [
        "<!-- avaliador-playlist -->",
        f"## 🎧 Avaliação automática — {spec['projeto']}",
        "",
        f"### Nota: **{total:.1f} / 100**  ({verdict}, mínimo {minimum:.0f})",
        "",
    ]

    if tampered:
        lines += [
            "> ⚠️ **Arquivos protegidos foram alterados neste PR.**",
            "> A versão original foi restaurada antes da avaliação. "
            "Reverta essas alterações antes de pedir revisão.",
            "",
        ]

    lines += ["| | Requisito | Resultado | Peso | Nota |", "|---|---|---|---|---|"]
    for row in rows:
        lines.append(
            f"| {status_icon(row['ratio'])} | {row['id']}. {row['titulo']} "
            f"| {row['detalhe']} | {row['peso']:.0f} | {row['nota']:.1f} |"
        )
    lines.append("")

    failures = [
        (row, case)
        for row in rows
        for case in row["casos"]
        if not case[1]
    ]
    if failures:
        lines += ["<details><summary>🔍 Testes que ainda não passam</summary>", ""]
        current = None
        for row, (name, _, detail) in failures:
            if row["id"] != current:
                current = row["id"]
                lines.append(f"**Requisito {row['id']} — {row['titulo']}**")
                lines.append("")
            lines.append(f"- `{name}`" + (f" — {detail}" if detail else ""))
        lines += ["", "</details>", ""]

    lines.append("_Este comentário é atualizado a cada novo push nesta branch._")
    return "\n".join(lines)


def main():
    args = parse_args()
    spec = json.loads(pathlib.Path(args.spec).read_text(encoding="utf-8"))
    results = read_reports(args.reports)
    tampered = args.tampered.lower() == "true"

    rows, total = evaluate(spec, results, args.checkstyle_status == 0, args.build_status == 0)
    markdown = render(spec, rows, total, tampered)

    pathlib.Path(args.out).write_text(markdown, encoding="utf-8")
    print(markdown)

    summary = os.environ.get("GITHUB_STEP_SUMMARY")
    if summary:
        with open(summary, "a", encoding="utf-8") as handle:
            handle.write(markdown + "\n")

    output = os.environ.get("GITHUB_OUTPUT")
    if output:
        with open(output, "a", encoding="utf-8") as handle:
            handle.write(f"nota={total:.1f}\n")
            handle.write(f"minimo={float(spec.get('notaMinima', 0)):.1f}\n")

    return 0


if __name__ == "__main__":
    sys.exit(main())

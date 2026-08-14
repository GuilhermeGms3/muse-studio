package com.musicos.config;

import com.musicos.domain.Assessment;
import com.musicos.domain.AssessmentRubricLevel;
import com.musicos.domain.LearningStage;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

final class AssessmentEditorialPolicy {
    record Protocol(boolean formal, String title, String purpose, String instructions,
                    String conditions, String allowedSupport, String inconclusiveRule,
                    List<AssessmentRubricLevel> rubricLevels) {}

    private static final Set<String> QUALITATIVE = Set.of(
            "forma", "textura", "funcao", "função", "pocket", "conducao", "condução",
            "contraste", "musicalidade", "arco", "coerencia", "coerência", "decisao-musical",
            "densidade", "recuperacao", "recuperação");

    private AssessmentEditorialPolicy() {}

    static Protocol forMission(String title, String objective, String conditions, String activityType,
                               Assessment.Type type, LearningStage stage, List<String> criteria) {
        var formal = !(stage == LearningStage.FIRST_STEPS
                && (type == Assessment.Type.FORMATIVE || "listen".equals(activityType)
                || "compare".equals(activityType)));
        var instructions = switch (type) {
            case DIAGNOSTIC -> "Realize primeiro sem consulta. Depois repita somente o trecho que separa as hipóteses e registre o que mudou.";
            case FORMATIVE -> "Compare duas tentativas curtas, alterando apenas a variável estudada. Registre o efeito percebido, sem concluir domínio.";
            case PERFORMANCE -> "Prepare a entrada, execute a forma prevista sem reiniciar e registre como recuperou a continuidade quando algo saiu do plano.";
            case APPLICATION -> "Use a habilidade no contexto musical indicado. Preserve as restrições e observe se ela continua funcional fora do exercício isolado.";
            case TRANSFER -> "Execute em dois contextos. Declare o que permaneceu invariável e qual adaptação foi necessária no segundo.";
            case RETENTION, REVIEW -> "Recupere sem abrir a Lesson. Use apoio apenas depois da primeira tentativa e compare a condição atual com a evidência anterior disponível.";
        };
        var support = switch (type) {
            case PERFORMANCE -> "Contagem inicial e mapa de forma; clique somente quando a Mission o prescrever.";
            case RETENTION, REVIEW -> "Nenhum apoio na primeira tentativa; depois, apenas a referência necessária para localizar a lacuna.";
            case TRANSFER -> "Referência no primeiro contexto; no segundo, somente a restrição escrita.";
            default -> "Material original da Lesson, contagem e metrônomo somente quando previstos nas condições.";
        };
        return new Protocol(formal, "Observação de " + title, objective, instructions, conditions, support,
                "Se o artefato ou a observação não expuser um critério, marque-o como inconclusivo; gravação armazenada não equivale a execução analisada.",
                rubrics(criteria));
    }

    private static List<AssessmentRubricLevel> rubrics(List<String> criteria) {
        var result = new ArrayList<AssessmentRubricLevel>();
        criteria.stream().filter(AssessmentEditorialPolicy::qualitative).forEach(key -> {
            result.add(new AssessmentRubricLevel(key, "EM_CONSTRUCAO",
                    "A intenção ainda não se mantém ou a restrição principal se perde durante a tentativa."));
            result.add(new AssessmentRubricLevel(key, "FUNCIONAL",
                    "A restrição é atendida e a função musical fica reconhecível, embora com escolhas ou transições instáveis."));
            result.add(new AssessmentRubricLevel(key, "CONSISTENTE",
                    "A decisão musical permanece clara ao longo da forma e sobrevive à mudança de contexto prevista."));
        });
        return List.copyOf(result);
    }

    private static boolean qualitative(String key) {
        var normalized = key.toLowerCase();
        return QUALITATIVE.contains(normalized)
                || QUALITATIVE.stream().anyMatch(normalized::contains);
    }
}

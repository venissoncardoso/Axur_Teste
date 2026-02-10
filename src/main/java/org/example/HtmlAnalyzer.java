
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Stack;

public class HtmlAnalyzer {
    public static void main(String[] args) {

        // Validação básica de argumento (Requisito 4 técnico)
        if (args.length == 0) {
            return;
        }

        String urlString = args[0];

        try {
            // Requisito Funcional: Obter conteúdo a partir de uma URL [cite: 4]
            URL url = new URL(urlString);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

            analyzeHtml(reader);

            reader.close();

        } catch (IOException e) {
            // Requisito 5.c: Mensagem exata em caso de erro de conexão [cite: 33]
            System.out.println("URL connection error");
        }
    }

    private static void analyzeHtml(BufferedReader reader) throws IOException {
        String line;

        // Stack para rastrear o aninhamento das tags (Profundidade)
        Stack<String> tags = new Stack<>();

        int currentDepth = 0;
        int maxDepth = -1; // Começa em -1 para garantir que qualquer texto seja capturado
        String deepestText = "";
        boolean malformed = false;

        // Requisito 1: O código HTML está dividido em linhas [cite: 12]
        while ((line = reader.readLine()) != null) {
            // Requisito: Ignorar espaços iniciais (indentação) [cite: 20]
            line = line.trim();

            // Requisito: Ignorar linhas em branco [cite: 21]
            if (line.isEmpty()) continue;

            // Lógica baseada na Premissa 2: A linha é tag de abertura, fechamento OU texto [cite: 13]

            if (isClosingTag(line)) {
                // Caso: Tag de Fechamento (ex: </div>) [cite: 15]
                String tagName = getTagName(line);

                // Validação de Mal-formado (Bônus):
                // Se a pilha está vazia (fechou sem abrir) OU a tag não bate com a última aberta
                if (tags.isEmpty() || !tags.pop().equals(tagName)) {
                    malformed = true;
                    break; // Interrompe pois já é inválido
                }

                // Ao fechar tag, diminuímos a profundidade (Stack diminui naturalmente)

            } else if (isOpeningTag(line)) {
                // Caso: Tag de Abertura (ex: <div>) [cite: 14]
                // Empilhamos a tag para aumentar a profundidade
                tags.push(getTagName(line));

            } else {
                // Caso: Trecho de texto (ex: "Este é o corpo.") [cite: 16]
                // A profundidade atual é o tamanho da pilha
                currentDepth = tags.size();

                // Requisito: Se dois trechos tiverem a mesma profundidade, retorna o PRIMEIRO
                // Por isso usamos ">" estrito. Se for igual, mantém o antigo (que apareceu primeiro).
                if (currentDepth > maxDepth) {
                    maxDepth = currentDepth;
                    deepestText = line;
                }
            }
        }

        // Output Final conforme Requisito 5 [cite: 30]

        // Se a flag malformed estiver true OU se sobraram tags na pilha (não foram fechadas)
        if (malformed || !tags.isEmpty()) {
            System.out.println("malformed HTML");
        } else {
            System.out.println(deepestText); // Retorna o trecho identificado [cite: 31]
        }
    }

    // Premissa 5: Tags de abertura não possuem atributos e terminam com ">" [cite: 19]
    private static boolean isOpeningTag(String line) {
        return line.startsWith("<") && !line.startsWith("</") && line.endsWith(">");
    }

    // Premissa 4: Apenas elementos com pares de abertura/fechamento [cite: 18]
    private static boolean isClosingTag(String line) {
        return line.startsWith("</") && line.endsWith(">");
    }

    // Método auxiliar para extrair "div" de "<div>" ou "</div>"
    private static String getTagName(String line) {
        if (line.startsWith("</")) {
            return line.substring(2, line.length() - 1);
        } else {
            return line.substring(1, line.length() - 1);
        }
    }
}
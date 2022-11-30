## Objetivo
Essa API implementa um procedimento de decisão para validade e consequência de inclusões no fragmento geométrico de BGL (Basic Graph Logic).

Dado uma inclusão entre grafos de GeoBGL, o procedimento coloca os grafos em forma normal e verifica se há homomorfismo entre eles.

[Visualize o Projeto de Front-End aqui!](https://github.com/igorcruzf/diagrammatic-proof-frontend)

## Como executar o projeto localmente

Para executar a aplicação é necessário ter java 17 configurado na máquina e rodar o comando
``.\gradlew bootRun`` na raiz do projeto.

### A aplicação está disponível remotamente via plataforma do Heroku.
## Endpoint

### /diagrams/validate-homomorphism?expression={{expression}}&hypotheses={{hypotheses}}

### Exemplo:
    curl --location --request GET 'https://diagrammatic-proof-service.herokuapp.com/diagrams/validate-homomorphism?expression=(AintB)inv%20inc%20(A%20int%20B)%20inv&hypotheses=A%20inc%20C,%20C%20inc%20A'
    
ou pelo navegador: https://diagrammatic-proof-service.herokuapp.com/diagrams/validate-homomorphism?expression=(AintB)inv%20inc%20(A%20int%20B)%20inv&hypotheses=A%20inc%20C,%20C%20inc%20A

### Documentação via swagger / OpenAPI: https://diagrammatic-proof-service.herokuapp.com/swagger-ui/index.html

#### Entrada:
    
Uma expressão de subconjunto por exemplo:
  
    (A int B)inv inc A

- É necessário passar o comando inc (que representa a inclusão) separando os dois lados da expressão.
- Qualquer termo pode ser uma letra ou nome qualquer desde que não seja uma operação ou o símbolo da inclusão, ex: A, banana, b
- A aplicação é case sensitive: A é diferente de a, inc é diferente de INC e assim por diante.
- Operações disponíveis:
  - int = interseção
  - inv = inversa
  - comp = composição
- Símbolo da inclusão = inc
- Pode ser utilizado espaços e parentêses para organização
- Recomendado usar o parênteses na inversão, exemplo:
  - (Ainv) 
  - A int (Binv)
  - ((A int B)inv)

Uma lista de hipóteses que podem ser aplicadas para verificação da inclusão.
- Cada hipótese segue as mesmas características da expressão explicada acima.

#### Saída:

A saída é o objeto de contramodelo e 
dois objetos "diagrammatic_proof", um para o
lado esquerdo da expressão e o outro pro direito.

Cada objeto "diagrammatic_proof" tem uma lista de diagramas e cada diagrama possui:

  - lista de nós (nodes):
    - todos os nós existentes nesse diagrama
    - cada nó possui:
      - um nome (utilizado para identificar o nó)
      - um tipo que pode ser:
        - INPUT -> nó de entrada do diagrama
        - OUTPUT -> nó de saída do diagrama
        - INTERMEDIATE -> nó intermediário gerado na transformação para o diagrama normal
  - lista de arestas (edges):
    - todas as arestas nesse diagrama
    - cada aresta possui:
      - um nó da esquerda dessa aresta
      - um rótulo 
      - um nó da direita
    - cada rótulo possui:
      - um nome, se for um termo atômico (sem operação)
      - ou um termo, uma operação e, caso a operação não seja a inversa, outro termo
  - descrição do passo (step_description):
    - informação de que transformação foi feita para chegar nesse diagrama:
      - BEGIN - início
      - REMOVE_INTERSECTION - transformação de uma aresta com interseção em duas arestas
      - REMOVE_COMPOSITION - transformação de uma aresta com composição em duas arestas
      - REMOVE_INVERSE - transformação de uma aresta com inversa em uma aresta com a posição dos nós invertidas
      - Adding {{expressão}} with hypothesis - adição da expressão direita da hipótese no diagrama
O último diagrama de cada diagrammatic_proof é o diagrama na forma normal em que não há mais operações a serem transformadas.

Se houver homomorfismo, cada nó no diagrama normal do "right_diagrammatic_proof" possuirá
um atributo "image_name" com o nome do nó no diagrama normal do "left_diagrammatic_proof" que é a imagem
dele.

O objeto "countermodel" tem:
 - a informação se existe ou não homomorfismo no campo "is_homomorphic"
 - um objeto "universe", que associa cada nó da forma normal do diagrama à esquerda como um número inteiro para variáveis desse universo
 - um objeto "relations":
   - associa pares de variáveis do universo deste contramodelo para cada rótulo da forma normal do diagrama à esquerda
   - associa o par (-1, -1) para representar o conjunto vazio quando o rótulo está presente apenas no diagrama da direita
   - associa pares para aplicações de operações em cima das relações, utilizando os pares descritos acima.

## Objetivo
Essa API implementa um procedimento de decisão para validade de inclusões no fragmento geométrico de BGL (Basic Graph Logic).

Dado uma inclusão entre grafos de GeoBGL, o procedimento coloca os grafos em forma normal e verifica se há homomorfismo entre eles.


## Como executar

Para executar a aplicação é necessário ter java 17 configurado na máquina e rodar o comando
``.\gradlew bootRun`` na raiz do projeto.

## Endpoint

### /diagrams/validate-homomorphism?expression={{expression}}

### Exemplo:
    curl --location --request GET 'localhost:8080/diagrams/validate-homomorphism?expression=(AintB)invinc(AintB)inv'
#### Entrada:
    
Uma expressão de subconjunto por exemplo:
  
    (A int B)inv inc A

- É necessário passar o comando inc no meio da expressão
- Qualquer termo pode ser uma letra ou nome qualquer desde que não seja uma operação ou o símbolo da inclusão, ex: A, banana, b
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

#### Saída:

A saída é a informação se existe ou não homomorfismo e 
dois objetos "diagrammatic_proof", um para o
lado esquerdo da expressão e o outro pro direito.

Cada objeto tem uma lista de diagramas e cada diagrama possui:

  - lista de arestas (edges):
    - todas as arestas nesse diagrama
    - cada aresta possui um nó da esquerda dessa aresta, um termo e um nó da direita
    - cada rótulo possui:
      - um nome se for um termo atômico (sem operação)
      - ou um termo, uma operação e, caso a operação não seja a inversa, outro termo 
  - lista de nós (nodes):
    - todos os nós existentes nesse diagrama
    - cada nó possui um nome e um tipo
  - descrição do passo (step_description):
    - informação de que transformação foi feita para chegar nesse diagrama:
      - BEGIN - início
      - REMOVE_INTERSECTION - transformação de uma aresta com interseção em duas arestas
      - REMOVE_COMPOSITION - transformação de uma aresta com composição em duas arestas
      - REMOVE_INVERSE - transformação de uma aresta com inversa em uma aresta com a posição dos nós invertidas
  
O último diagrama de cada diagrammatic_proof é o diagrama na forma normal em que não há mais operações a serem transformadas.

Se houver homomorfismo, cada nó no diagrama normal do "right_diagrammatic_proof" possuirá
um atributo "image_name" com o nome do nó no diagrama normal do "left_diagrammatic_proof" que é a imagem
dele.


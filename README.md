## Objetivo
Essa API tem como objetivo transformar diagramas para cálculo por grafo e
verificar se há homomorfismo entre os diagramas, com objetivo de verificar
se a inclusão é verdadeira ou não.


## Como executar

Para executar a aplicação é necessário ter java 11 configurado na máquina e rodar o comando
``.\gradlew bootRun`` na raiz do projeto.

## Endpoint

### /diagrams/validate-homomorphism?expression={{expression}}

### Exemplo:
    curl --location --request GET 'localhost:8080/diagrams/validate-homomorphism?expression=(A%5CcapB)%5Cinv%5Csubseteq(A%5CcapB)%5Cinv'
#### Entrada:
    
Uma expressão de subconjunto por exemplo:
  
    (A %5Ccap B)%5Cinv %5Csubseteq A

- O comando \ é codificado para passar no parâmetro como %5C
- É necessário passar o comando %5Csubseteq no meio da expressão
- Qualquer termo pode ser uma letra ou nome qualquer, ex: A, banana, b
- Operações disponíveis:
  - %5Ccap (\cap) = interseção
  - %5Cinv (\inv) = inversa
  - %5Ccirc (\circ) = composição
  - %5Csubseteq (\subseteq) = inclusão
- Pode ser utilizado espaços e parentêses para organização
- Recomendado usar o parênteses na inversão, exemplo:
  - (A%5Cinv) 
  - A%5Ccap(B%5Cinv)
  - ((A%5CcapB)%5Cinv)

#### Saída:

A saída é a informação se existe ou não homomorfismo e 
dois objetos "diagrammatic_proof", um para o
lado esquerdo da expressão e o outro pro direito.

Cada objeto tem uma lista de diagramas e cada diagrama possui:

  - lista de arestas (edges):
    - todas as arestas nesse diagrama
    - cada aresta possui um nó da esquerda dessa aresta, um termo e um nó da direita
    - cada termo possui:
      - um nome se for atômico (sem operação)
      - ou um termo, uma operação e outro termo (caso a operação não seja a inversa)
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
um atributo "node_image" com o nome do nó no diagrama normal do "left_diagrammatic_proof" que é a imagem
dele.


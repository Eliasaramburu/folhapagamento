# Sistema de Gerenciamento de Funcionários e Folha de Pagamento

Sistema em Java, executado no console, que cadastra funcionários de três categorias (CLT, PJ e Comissionado), calcula suas remunerações e gera relatório e total da folha de pagamento. Desenvolvido com **Programação Orientada a Objetos**, arquitetura em **camadas** e **TDD** (JUnit 5).

**Integrantes:**
- Joel Elias Aramburu da Silva

## Funcionalidades

- Cadastrar funcionários (CLT, PJ e Comissionado)
- Listar todos os funcionários
- Buscar funcionário por código, por CPF ou por nome (exato ou parcial)
- Exibir os dados completos de um funcionário
- Calcular a remuneração de um funcionário
- Remover um funcionário
- Gerar relatório com todos os funcionários e suas remunerações
- Calcular o total da folha de pagamento

## Tecnologias

- Java 17 ou superior
- Maven
- JUnit 5 (testes automatizados)
- Armazenamento em memória (`LinkedHashMap`), sem banco de dados

## Como executar o projeto

Pré-requisitos: **JDK 17+** e **Maven** instalados (`java -version` e `mvn -v`).

```bash
# 1) Executar direto pelo Maven
mvn compile exec:java

# 2) Ou gerar o .jar e executá-lo
mvn package -DskipTests
java -jar target/folha-pagamento-1.0.0.jar
```

Também é possível abrir a pasta na IDE (IntelliJ IDEA, Eclipse ou NetBeans) como projeto Maven e executar a classe `Main`.

O programa abre um menu numerado; digite o número da opção e siga as perguntas. Valores decimais aceitam ponto ou vírgula (`3000,50`).

## Como executar os testes

```bash
mvn test
```

Os testes cobrem cenários de sucesso e de falha, o lançamento das exceções personalizadas, o cálculo de remuneração de todas as categorias e o comportamento polimórfico. Existe uma classe de teste para cada classe de produção (exceto `Main`).

## Estrutura do projeto

```
src/
 ├── main/java/
 │    ├── Main.java
 │    ├── model/
 │    │    ├── Identificavel.java            (interface)
 │    │    ├── Tributavel.java               (interface)
 │    │    ├── Funcionario.java              (classe abstrata)
 │    │    ├── FuncionarioCLT.java
 │    │    ├── FuncionarioPJ.java
 │    │    └── FuncionarioComissionado.java
 │    ├── repository/FuncionarioRepository.java
 │    ├── service/FuncionarioService.java
 │    ├── view/FuncionarioView.java
 │    ├── exception/
 │    │    ├── FuncionarioNaoEncontradoException.java
 │    │    ├── FuncionarioJaCadastradoException.java
 │    │    └── DadosInvalidosException.java
 │    └── util/ValidadorCpf.java
 └── test/java/                              (espelha a estrutura acima)
```

| Camada | Responsabilidade |
|--------|------------------|
| **model** | Entidades e regras de remuneração de cada categoria |
| **repository** | Armazenar e recuperar funcionários |
| **service** | Regras de negócio: cadastro, buscas, remoção, relatório e folha |
| **view** | Menu de console; trata as exceções e mostra mensagens ao usuário |
| **exception** | Situações excepcionais do sistema |
| **util** | Validação, normalização e formatação de CPF |

## Regras de remuneração

| Categoria | Remuneração | Tributos (`Tributavel`) |
|-----------|-------------|-------------------------|
| **CLT** | salário-base + adicional de benefícios | 11% da remuneração |
| **PJ** | valor por projeto × quantidade de projetos | não se aplica (o PJ recolhe os próprios impostos) |
| **Comissionado** | salário-base + (vendas × percentual / 100) | 13% da remuneração |

- O **total da folha** é a soma da remuneração bruta de todos os funcionários.
- O **salário líquido** é a remuneração menos os tributos (igual à remuneração, no caso do PJ).
- O PJ não possui salário-base; ele fica em zero.
- As alíquotas são valores didáticos, definidos pelo grupo, e não refletem a legislação tributária real.

## Conceitos de POO aplicados

- **Encapsulamento:** todos os atributos são `private`; alterações passam por setters que validam os dados. O código do funcionário é imutável.
- **Herança e abstração:** `Funcionario` é abstrata e declara `calcularRemuneracao()`; as três subclasses implementam a sua regra.
- **Interfaces:** `Identificavel` (todas as classes, por herança) e `Tributavel` (apenas CLT e Comissionado, onde faz sentido).
- **Polimorfismo por sobreposição:** uma referência `Funcionario` executa a regra do objeto real (ver `PolimorfismoTest`).
- **Polimorfismo por sobrecarga:** `FuncionarioService.buscarFuncionario(int codigo)`, `buscarFuncionario(String cpf)` e `buscarFuncionario(String nome, boolean buscaExata)`.

## Tratamento de exceções

| Exceção | Tipo | Quando é lançada |
|---------|------|------------------|
| `FuncionarioNaoEncontradoException` | verificada | buscar, consultar ou remover funcionário inexistente |
| `FuncionarioJaCadastradoException` | verificada | código (ou CPF) já cadastrado |
| `DadosInvalidosException` | não verificada | CPF inválido, valores negativos, percentual fora de 0–100, dados obrigatórios ausentes |

As exceções são lançadas pelas camadas de modelo, repositório e serviço e **tratadas na camada de visão**, que exibe a mensagem e mantém o menu funcionando.

## Desenvolvimento orientado a testes (TDD)

O projeto foi construído em ciclos **Red → Green → Refactor**, e o histórico do Git registra cada etapa pelo prefixo da mensagem:

- `test(red): ...` — testes escritos antes da implementação (não compilam ou falham);
- `feat(green): ...` — implementação mínima para os testes passarem;
- `refactor: ...` — melhoria do código com os testes verdes.

Ordem dos ciclos: exceções e validador de CPF → núcleo do modelo (`Funcionario`, CLT, interfaces) → PJ, Comissionado e polimorfismo → refactor das validações → repositório → serviço → visão e `Main`.

Para ver o histórico: `git log --oneline`.

# 🔥 Sistema de Monitoramento de Queimadas no Maranhão

### Universidade Federal do Maranhão (UFMA)
**Disciplina:** Paradigmas de Programação  
**Integração acadêmica com a disciplina:** Banco de Dados
**Professora:** Vandecia Rejane Monteiro Fernandes  
**Discentes:** Kely Souza, Gabriel Gama e Matheus Alexsander  
**Avaliação:** 3ª Avaliação — 2026

---

## 📌 Sobre o projeto

O **Sistema de Monitoramento de Queimadas no Maranhão** é uma aplicação desktop desenvolvida em Java, utilizando a biblioteca **Swing** para a interface gráfica e o banco de dados **PostgreSQL** para armazenamento permanente das informações.

O sistema tem como objetivo auxiliar no registro, na organização e no gerenciamento de ocorrências de queimadas em municípios do estado do Maranhão.

A aplicação permite cadastrar, consultar, editar e excluir ocorrências, relacionando cada registro a um município e a um agente responsável.

O projeto foi desenvolvido com finalidade acadêmica, aplicando conceitos de **Programação Orientada a Objetos**, **Collections**, **persistência de dados**, **SQL** e **modelagem de banco de dados**.

## 🎯 Objetivos

### Objetivo geral

Desenvolver uma aplicação desktop para gerenciamento de ocorrências de queimadas no Maranhão, utilizando Java Swing e integração com banco de dados PostgreSQL.

### Objetivos específicos

- Aplicar os conceitos de herança, polimorfismo e abstração.
- Desenvolver uma interface gráfica utilizando Java Swing.
- Implementar operações de cadastro, consulta, edição e exclusão.
- Utilizar Collections para manipulação de conjuntos de objetos.
- Realizar a persistência dos registros em banco de dados relacional.
- Organizar informações sobre municípios, agentes, equipes e ocorrências.
- Aplicar conceitos de modelagem conceitual, lógica e física de banco de dados.

## ⚙️ Funcionalidades do sistema

### 1. Cadastrar Ocorrência

Permite registrar novas ocorrências de queimadas, informando:

- Município;
- Bairro ou localidade;
- Tipo de ocorrência: rural ou urbana;
- Data;
- Temperatura;
- Umidade;
- Descrição;
- Situação;
- Agente responsável.

### 2. Consultar Ocorrências

Apresenta os registros cadastrados em uma tabela, permitindo visualizar informações detalhadas de cada ocorrência.

A consulta exibe o município, o tipo de ocorrência, o agente responsável, a situação e a localização, além dos demais dados ao selecionar um registro.

### 3. Editar Ocorrência

Permite atualizar informações de ocorrências anteriormente cadastradas.

### 4. Excluir Ocorrência

Permite excluir registros existentes mediante confirmação do usuário.

### 5. Cadastrar Municípios

Permite incluir novos municípios diretamente pela interface gráfica.

### 6. Cadastrar Agentes

Permite registrar agentes responsáveis pelas ocorrências, informando:

- Nome;
- CPF;
- Equipe responsável.

O CPF substitui o antigo campo de matrícula. No banco de dados, `id_agente` permanece como chave primária, enquanto `cpf` é um atributo único.

### 7. Índice de Risco Estimado

O sistema realiza um cálculo demonstrativo baseado na temperatura e na umidade informadas.

# 8. Cálculo do índice de risco

O sistema utiliza fórmulas didáticas distintas para as ocorrências rurais e urbanas.

**Queimada rural:**

`R = (temperatura × 1,5) − (umidade × 0,5)`

**Queimada urbana:**

`R = (temperatura × 1,2) − (umidade × 0,3)`

Os resultados são limitados ao intervalo de 0 a 100.

O cálculo utiliza implementações diferentes para ocorrências rurais e urbanas, demonstrando o conceito de polimorfismo.

**Observação:** esse índice possui finalidade didática e não representa um método científico validado de previsão de incêndios.

---
## 💻 Tecnologias utilizadas e requisitos

| Tecnologia | Especificação |
|---|---|
| Linguagem | Java 21 |
| JDK utilizado | Eclipse Temurin 21.0.13 (OpenJDK) |
| IDE | IntelliJ IDEA 2026.3 |
| Interface gráfica | Java Swing (`javax.swing`) |
| Collections | Java Collections Framework (`ArrayList`) |
| PostgreSQL 18 | Banco de dados relacional |
| JDBC | Comunicação entre Java e PostgreSQL |
| pgJDBC 42.7.14 | Driver de conexão |
| pgAdmin 4 | Administração do banco de dados |
| SQL | Criação, consulta e manipulação de dados |
| Git e GitHub | Versionamento e disponibilização do projeto |
| diagrams.net | Elaboração dos modelos conceitual e lógico |

---


## 🧩 Programação Orientada a Objetos

O projeto utiliza conceitos fundamentais de orientação a objetos.

### Herança

A classe abstrata `Ocorrencia` representa os atributos e comportamentos comuns às ocorrências de queimadas.

As classes `QueimadaRural` e `QueimadaUrbana` herdam características dessa classe.

```java
abstract class Ocorrencia {
    public abstract double calcularRisco();
}
```

### Polimorfismo

O método `calcularRisco()` é implementado de maneira diferente nas subclasses.

Dessa forma, objetos de tipos diferentes podem responder à mesma operação com comportamentos específicos.

### Abstração

A classe `Ocorrencia` define a estrutura geral dos registros e estabelece comportamentos que devem ser implementados pelas subclasses.

### Collections

O sistema utiliza `ArrayList` para organizar e manipular conjuntos de ocorrências retornadas pelo banco de dados.

### Persistência de dados

Os registros são armazenados no PostgreSQL por meio de JDBC, utilizando comandos SQL e `PreparedStatement`.

Isso permite manter as informações cadastradas mesmo após o encerramento do aplicativo.

## 🗂️ Estrutura do projeto

```text
MonitoramentoQueimadas-Maranhao/
│
├── src/
│   ├── App.java
│   ├── Conexao.java
│   ├── GerenciadorOcorrencias.java
│   ├── Ocorrencia.java
│   ├── QueimadaRural.java
│   ├── QueimadaUrbana.java
│   └── TesteConexao.java
│
├── banco-de-dados/
│   ├── 01_MODELO_FISICO_BD.sql
│   ├── 02_DADOS_FICTICIOS.sql
│   ├── 03_CONSULTAS_AVALIACAO.sql
│   └── 04_TESTE_TRIGGER_SEM_PERSISTIR.sql
│
├── documentacao/
│   ├── Modelos_Conceitual_E_Logico.pdf
│   ├── Modelos_Conceitual_e_Logico_PRONTO_GITHUB.drawio
│   └── Relatorios_academicos.pdf
│
├── diagrama-classes.puml
├── README.md
└── .gitignore
```

### Principais classes

| Classe | Responsabilidade |
|---|---|
| `App` | Interface gráfica e interação com o usuário |
| `Conexao` | Conexão com o PostgreSQL |
| `GerenciadorOcorrencias` | Gerenciamento e persistência das ocorrências |
| `Ocorrencia` | Classe abstrata que representa uma ocorrência |
| `QueimadaRural` | Especialização de ocorrência rural |
| `QueimadaUrbana` | Especialização de ocorrência urbana |
| `TesteConexao` | Verificação da conexão com o banco |

---

## 🗄️ Banco de Dados

O sistema utiliza um banco de dados relacional desenvolvido em PostgreSQL.

A modelagem contempla cinco entidades principais:

1. **Município:** armazena os municípios cadastrados.
2. **Equipe:** representa as equipes responsáveis pelo atendimento.
3. **Agente:** registra os agentes vinculados às equipes.
4. **Ocorrência:** armazena os registros das queimadas.
5. **Atendimento:** representa a associação entre equipes e ocorrências.

### Relacionamentos

- Município (1:N) Ocorrência.
- Agente (1:N) Ocorrência.
- Equipe (1:N) Agente.
- Equipe (N:N) Ocorrência, por meio de Atendimento.

### Especialização

A entidade Ocorrência possui duas especializações:

- Ocorrência Rural;
- Ocorrência Urbana.

No modelo lógico e físico, a especialização é representada pelo atributo `tipo_ocorrencia`, na tabela `ocorrencia`.

### Modelos desenvolvidos

O projeto contém:

- **Modelo conceitual:** desenvolvido na notação Entidade-Relacionamento de Peter Chen.
- **Modelo lógico:** representação das tabelas, atributos, chaves primárias e estrangeiras.
- **Modelo físico:** implementação das estruturas em PostgreSQL.

Os arquivos dos modelos conceitual e lógico estão disponíveis em PDF e em formato editável `.drawio`, dentro da pasta `documentacao`.

### Recursos SQL

O projeto também apresenta:

- Consultas com funções agregadas;
- Agrupamentos com `GROUP BY`;
- Consultas com `HAVING`;
- Função armazenada;
- Trigger;
- Relacionamentos com chaves primárias e estrangeiras.

A trigger foi definida para atualizar a situação de uma ocorrência quando todos os atendimentos associados estiverem concluídos.

---

## 🚀 Como executar o projeto

### Pré-requisitos

É necessário possuir:

- Java JDK 21;
- IntelliJ IDEA ou outra IDE compatível;
- PostgreSQL;
- Driver JDBC do PostgreSQL;
- Banco de dados configurado.

|            |              
### 1. Clonar o repositório

```bash
git clone https://github.com/kelyazuos/MonitoramentoQueimadas-Maranhao.git
```

### 2. Abrir o projeto

Abra a pasta clonada no IntelliJ IDEA.

Configure o JDK 21 nas opções do projeto.

### 3. Configurar o driver JDBC

Adicione o driver PostgreSQL às dependências do projeto, caso ainda não esteja configurado.

Driver utilizado no desenvolvimento:

`postgresql-42.7.14.jar`

### 4. Preparar o banco de dados

No pgAdmin 4, crie um banco de dados para a aplicação.

Execute os scripts SQL na seguinte ordem:

1. `01_MODELO_FISICO_BD.sql` — criação das tabelas e demais estruturas.
2. `02_DADOS_FICTICIOS.sql` — inserção de dados para demonstração.

O primeiro script foi preparado para um **banco novo e vazio**. Não deve ser executado novamente em um banco que já contém as tabelas do projeto.

Os demais scripts apresentam consultas e demonstrações dos recursos de Banco de Dados.

### 5. Configurar a conexão

Abra a classe `Conexao.java` e confira o endereço do banco.

Exemplo de configuração utilizada no desenvolvimento:

```text
jdbc:postgresql://localhost:5432/monitoramento_queimadas
```
.
A senha do PostgreSQL é solicitada durante a execução do aplicativo.


### 6. Executar a aplicação

Execute o método `main()` da classe `App.java`.

A interface gráfica será aberta, permitindo acessar as funcionalidades disponíveis.

---

## 🔒 Segurança e privacidade

O projeto utiliza informações fictícias para fins acadêmicos.

Os números de CPF utilizados em demonstrações não representam cadastros reais intencionalmente.

Senhas de acesso ao PostgreSQL não devem ser publicadas no repositório.

## 📚 Contexto acadêmico

O projeto foi desenvolvido para aplicar os conhecimentos adquiridos nas disciplinas da Universidade Federal do Maranhão.

### Paradigmas de Programação

São utilizados os conceitos de:

- Interface gráfica com Java Swing;
- Cadastro, alteração e exclusão de registros;
- Herança;
- Polimorfismo;
- Abstração;
- Collections;
- Armazenamento permanente com banco de dados.

### Banco de Dados

São apresentados:

- Definição e modelagem do problema;
- Modelo conceitual;
- Modelo lógico;
- Modelo físico;
- Relacionamentos 1:N e N:N;
- Especialização;
- Consultas SQL;
- Função armazenada;
- Trigger.

## 📄 Considerações finais

O Sistema de Monitoramento de Queimadas no Maranhão demonstra a integração entre programação orientada a objetos e banco de dados relacional.

A aplicação reúne uma interface gráfica em Java Swing, classes organizadas com herança e polimorfismo e mecanismos de persistência com PostgreSQL.

O projeto possui finalidade educacional e constitui uma aplicação demonstrativa de gerenciamento de ocorrências, não um sistema oficial de monitoramento ambiental em tempo real.

---

## 👩‍💻 Equipe de desenvolvimento

- Kely Souza
- Gabriel Gama
- Matheus Alexsander

**Projeto:** Sistema de Monitoramento de Queimadas no Maranhão

**Ano:** 2026

---

**Repositório oficial do projeto:**  
https://github.com/kelyazuos/MonitoramentoQueimadas-Maranhao

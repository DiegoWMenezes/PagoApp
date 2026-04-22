# PagoApp

App Android para controle de pagamentos pessoais com metodos — Pix, Cartao de Credito, Cartao de Debito, Boleto e TED.


## Telas

| Tela | O que faz |
|---|---|
| **Dashboard** | Mostra saldo, receitas, despesas, gastos por tipo de pagamento e transacoes recentes |
| **Transacoes** | Lista todas as transacoes com filtros por tipo, status, categoria e periodo |
| **Nova Transacao** | Formulario para registrar um pagamento com validacao e autocomplete de contatos |
| **Relatorios** | Tendencia mensal de gastos, top destinatarios e gastos por categoria |

## Como Rodar

1. Abrir no Android Studio
2. Sync Gradle
3. Rodar no emulador ou dispositivo (minSdk 26)

## Stack

- Kotlin + Jetpack Compose + Material3
- Room (banco de dados local)
- Hilt (injecao de dependencia)
- MVVM + Clean Architecture

## Estrutura

```
app/src/main/java/com/diegowmenezes/pagoapp/
├── PagoAppApplication.kt
├── MainActivity.kt
├── data/       → Banco, DAOs, entidades, mappers, repositories
├── domain/      → Modelos, interfaces de repository, use cases
├── di/          → Modulos Hilt (banco, repositories)
└── ui/          → Telas, ViewModels, componentes, tema, navegacao
```

## Testes

- ~20 testes unitarios (ViewModels, repositories, use cases) com MockK + Turbine
- ~10 testes instrumentados (queries Room) com banco in-memory


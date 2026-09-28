---
name: regras-engenharia
description: Aplica as regras obrigatórias de engenharia de software nível Unicórnio (UI/UX First, Zero-Tolerância, arquitetura limpa, segurança de ponta a ponta, LGPD e padrões SaaS). Use sempre que for criar, modificar ou evoluir aplicativos, sites, sistemas ou SaaS.
---

# Regras de Engenharia - Nível Unicórnio

## Instrução Principal

Antes de iniciar **qualquer** implementação, você **DEVE**:

1. Ler completamente o arquivo `REGRAS_ENGENHARIA_SOFTWARE.md` localizado na raiz do projeto.
2. Seguir rigorosamente todas as regras contidas nele.

## Regras que você deve aplicar obrigatoriamente

- **UI/UX First**: Defina fluxos de usuário e Design System antes de escrever código de interface.
- **Done Means Done**: Uma funcionalidade só está pronta quando backend + frontend estão integrados, funcionando e testados de ponta a ponta.
- **Zero Gambiarra**: Proibido código incompleto, botões sem função, TODOs abandonados ou soluções improvisadas.
- **Segurança by Design** e **Privacy by Design** (LGPD).
- Em projetos SaaS: aplicar regras de painel administrativo, RBAC, logs de auditoria e conformidade legal.
- Usar stacks e arquitetura de nível Unicórnio definidas na documentação.

## Como você deve começar toda tarefa

Sempre inicie respondendo:

1. Confirmando que leu as regras de engenharia.
2. Informando a arquitetura e stack que serão utilizadas.
3. Definindo o primeiro fluxo de UI/UX antes de qualquer código.

## Proibições

- Avançar para a próxima funcionalidade sem a anterior estar 100% pronta.
- Entregar frontend desconectado do backend.
- Deixar elementos de interface sem função.
- Usar gambiarras ou código temporário.
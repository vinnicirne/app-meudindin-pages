# 📐 DOCUMENTAÇÃO TÉCNICA DE ENGENHARIA DE SOFTWARE
## Nível Unicórnio

**Versão:** 1.7  
**Data:** Setembro 2026  
**Objetivo:** Este documento define as regras obrigatórias de engenharia de nível mundial que todo agente de IA deve seguir ao criar, modificar ou evoluir aplicativos, sites, sistemas e SaaS.  
**Padrão de qualidade:** Unicórnio — escalável, seguro, performático, elegante e à prova de falhas.  
**Uso obrigatório:** O agente deve **sempre** consultar e aplicar estas regras antes de iniciar qualquer implementação.  
**Método de trabalho:** Sempre seguir o **Checklist Passo a Passo** definido neste documento. Nunca avançar sem concluir o passo anterior.

---

## 1. Princípios Fundamentais (Nível Unicórnio)

| Princípio                  | Descrição                                                                                            |
| -------------------------- | ---------------------------------------------------------------------------------------------------- |
| **SOLID**                  | Single Responsibility, Open/Closed, Liskov Substitution, Interface Segregation, Dependency Inversion |
| **DRY**                    | Don't Repeat Yourself                                                                                |
| **KISS**                   | Keep It Simple, Stupid                                                                               |
| **YAGNI**                  | You Aren't Gonna Need It                                                                             |
| **Separation of Concerns** | Separação clara de responsabilidades                                                                 |
| **Clean Code**             | Código legível, coeso e manutenível                                                                  |
| **Zero Gambiarra**         | Proibido soluções improvisadas ou código temporário                                                  |
| **Done means Done**        | Funcionalidade só está pronta quando 100% funcional de ponta a ponta                                 |
| **Security by Design**     | Segurança desde o primeiro dia                                                                       |
| **Privacy by Design**      | Privacidade nativa (LGPD)                                                                            |
| **UI/UX First**            | Experiência do usuário é definida e validada antes do código                                         |
| **Unicorn Standard**       | Tudo deve ser construído como se fosse uma empresa de bilhão de dólares                              |

---

## 2. Checklist Passo a Passo (Obrigatório para o Agente)

O agente **nunca** deve pular etapas. Seguir rigorosamente esta sequência:

### Fase 1 — Entendimento e Planejamento
- [ ] 1.1 Ler e internalizar este documento completo
- [ ] 1.2 Entender o problema e o público-alvo
- [ ] 1.3 Preencher o **Prompt Mestre de Geração UI** (Seção 3)
- [ ] 1.4 Definir os fluxos principais do usuário (User Flows)
- [ ] 1.5 Definir a hierarquia de informação
- [ ] 1.6 Definir o Design System (tokens + componentes)
- [ ] 1.7 Definir arquitetura e stack
- [ ] 1.8 Planejar a estrutura de pastas e separação Dashboard Admin × Área do Usuário

### Fase 2 — Backend
- [ ] 2.1 Implementar a camada Domain
- [ ] 2.2 Implementar a camada Application (casos de uso)
- [ ] 2.3 Implementar a camada Infrastructure
- [ ] 2.4 Criar e testar as APIs
- [ ] 2.5 Validar autenticação, autorização e segurança
- [ ] 2.6 Escrever testes (unitários + integração)
- [ ] 2.7 Backend 100% funcional e testado

### Fase 3 — Frontend
- [ ] 3.1 Implementar o Design System
- [ ] 3.2 Criar os componentes base (shadcn personalizado)
- [ ] 3.3 Implementar as telas consumindo a API real
- [ ] 3.4 Implementar todos os estados (loading, empty, error, success)
- [ ] 3.5 Implementar microinterações com Framer Motion
- [ ] 3.6 Garantir responsividade e acessibilidade
- [ ] 3.7 Validar performance (Core Web Vitals)

### Fase 4 — Integração e Qualidade
- [ ] 4.1 Integrar frontend + backend
- [ ] 4.2 Testar fluxos de ponta a ponta
- [ ] 4.3 Validar segurança
- [ ] 4.4 Validar LGPD (quando aplicável)
- [ ] 4.5 Validar Zero-Tolerância
- [ ] 4.6 Validar Checklist Final

> **Regra de Ouro:** Só avance para a próxima fase quando a atual estiver 100% concluída.

---

## 3. Prompt Mestre de Geração UI (Obrigatório)

Antes de gerar qualquer interface, o agente **deve** preencher e seguir o Prompt Mestre abaixo.  
Este prompt é **adaptável** para qualquer tipo de produto (Mobile, Web, SaaS, Dashboard Admin, Landing, etc.) e **proíbe** a cópia de estruturas genéricas entre projetos.

```markdown
# Prompt Mestre de Geração de Interface — Nível Unicórnio

## 1. Contexto do Produto
- **Nome do produto:** [NOME]
- **Tipo de aplicação:** [Mobile App / Web App / SaaS / Dashboard Admin / Landing Page / Outro]
- **Público-alvo:** [descrever]
- **Problema principal que resolve:** [descrever]
- **Plataforma principal:** [iOS/Android / Web / Ambos]
- **Modo de visualização preferencial:** [Mobile-first / Desktop-first / Responsivo completo]

## 2. Objetivos de Experiência
- A interface deve transmitir: [ex: clareza, velocidade, sofisticação, foco, controle, leveza...]
- Referências de qualidade (inspiração de nível, nunca cópia): [ex: Linear, Things 3, Notion, Stripe, Vercel, Arc, Raycast...]
- O usuário nunca deve se sentir perdido ou sobrecarregado.

## 3. Design System (Obrigatório)
### Cores
- Fundo principal:
- Superfícies / Cards:
- Cor primária (ações principais):
- Cor de destaque / acento:
- Cores semânticas (sucesso, aviso, erro, informação):
- Textos (primário, secundário, terciário):

### Tipografia
- Família principal:
- Hierarquia (títulos, subtítulos, corpo, legendas):

### Estilo Visual
- Border radius:
- Sombras:
- Espaçamentos (escala):
- Estilo geral: [limpo / sóbrio / premium / minimalista / etc.]

### Componentes Base
- Botões (primário, secundário, ghost, destructive)
- Inputs e formulários
- Cards
- Navegação
- Estados obrigatórios: default, hover, focus, active, disabled, loading, empty, error, success

## 4. Estrutura de Navegação e Áreas
- Tipo de navegação: [Bottom Tab / Sidebar / Top Navigation / Hybrid / Outro]
- Existe separação entre **Área do Usuário** e **Dashboard Admin**?
  - Se sim:
    - Área do Usuário → rota base: [ex: /app]
    - Dashboard Admin → rota base: `/dashboard` (obrigatório)
- Principais seções/telas do produto:

## 5. Escopo das Telas / Fluxos a serem gerados
Liste as telas ou fluxos prioritários com descrição clara do que cada uma deve conter e qual o objetivo de uso.

Formato obrigatório:
1. **Tela X – Nome**
   - Objetivo:
   - Elementos principais:
   - Estados especiais (empty, loading, etc.):
   - Interações importantes:

## 6. Regras de Qualidade Obrigatórias (Nível Unicórnio)
- UI e UX devem ser premium e não genéricas
- Nenhuma tela deve parecer template ou “feita por IA”
- Feedback visual imediato em todas as ações
- Empty states bem pensados e com call-to-action
- Microinterações sutis e com propósito
- Acessibilidade básica (contraste, foco, hierarquia)
- Mobile-first quando aplicável
- Consistência total entre todas as telas
- Nunca deixar elementos sem função

## 7. Stack Visual Preferencial
- Base: Tailwind CSS + shadcn/ui + Radix UI + Framer Motion
- Ícones: Lucide ou Phosphor
- Tudo deve ser altamente personalizado (proibido visual padrão do shadcn)

## 8. Entrega Esperada
- Descrição detalhada de cada tela
- Estrutura de componentes
- Estados de cada componente importante
- Sugestão de hierarquia visual e espaçamentos
- Observações de usabilidade e microinterações
```

### Regras de Uso do Prompt Mestre

1. O Prompt Mestre **deve ser preenchido** antes de qualquer geração de interface.
2. Cada produto deve ter seu próprio preenchimento (proibido reutilizar telas de outro projeto).
3. O agente deve adaptar o prompt ao tipo de aplicação (mobile, web, SaaS, admin, etc.).
4. As seções 6, 7 e 8 são imutáveis e sempre devem ser respeitadas.
5. O resultado final deve obedecer a todas as regras deste documento (especialmente UI/UX de excelência e separação Dashboard Admin × Área do Usuário).

---

## 4. UI e UX — Regras Obrigatórias

> **Regra de Ouro:**  
> Nenhuma linha de código de interface deve ser escrita antes de definir e validar a experiência do usuário.  
> **UI e UX devem ser perfeitas**, nunca genéricas.

### 4.1 Processo Obrigatório
1. Preencher o Prompt Mestre de Geração UI
2. Definir fluxos e hierarquia de informação
3. Estabelecer Design System próprio e sofisticado
4. Validar qualidade visual
5. Só então implementar

### 4.2 Princípios de UX
- Clareza acima de tudo
- Feedback imediato
- Prevenção de erros
- Recuperação fácil
- Acessibilidade WCAG 2.1 AA
- Mobile-first
- Performance percebida
- Menos é mais
- Excelência visual (proibido genérico)

### 4.3 Design System Formal
Todo projeto deve ter:
- Design Tokens (cores, tipografia, espaçamentos, radius, shadows)
- Componentes base documentados
- Tema (light/dark quando aplicável)
- Preferência por Storybook ou documentação equivalente

### 4.4 Bibliotecas de UI/UX Premium
| Camada                      | Biblioteca                          |
|-----------------------------|-------------------------------------|
| Base de componentes         | shadcn/ui + Radix UI                |
| Estilização                 | Tailwind CSS                        |
| Animações                   | Framer Motion                       |
| Ícones                      | Lucide React ou Phosphor Icons      |

**Proibido:** Material UI, Ant Design, Chakra UI padrão, Bootstrap.

**Stack oficial:** Next.js + TypeScript + Tailwind CSS + shadcn/ui + Radix UI + Framer Motion

---

## 5. Separação Obrigatória: Dashboard Admin × Área do Usuário

### 5.1 `/dashboard` = Painel Administrativo
- Sempre e exclusivamente o Painel de Controle Administrativo
- Acesso restrito (RBAC + MFA)
- Nunca misturar com a área do usuário

### 5.2 Área do Usuário
- Nunca usar a rota `/dashboard`
- Usar nomenclatura própria (`/app`, `/home`, `/workspace`, etc.)

| Conceito            | Rota padrão            | Público                          |
|---------------------|------------------------|-----------------------------------|
| Dashboard Admin     | `/dashboard`           | Controle total do sistema         |
| Área do Usuário     | `/app`, `/home`, etc.  | Experiência do produto            |

---

## 6. Stacks Recomendadas

### 6.1 Frontend Web
- Next.js (App Router) + TypeScript + Tailwind CSS
- UI/UX: shadcn/ui + Radix UI + Framer Motion
- Estado: Zustand ou TanStack Query
- Formulários: React Hook Form + Zod

### 6.2 Mobile
- React Native (Expo) + TypeScript ou Flutter

### 6.3 Backend
- NestJS + TypeScript ou Fastify
- Validação: Zod
- ORM: Prisma ou Drizzle

### 6.4 Banco de Dados
- PostgreSQL + Redis + BullMQ

### 6.5 Infraestrutura
- Docker + CI/CD + Vercel/Railway/AWS/GCP

### 6.6 Autenticação
- Auth.js, Clerk, Supabase Auth ou solução própria
- MFA obrigatório no `/dashboard`

---

## 7. Arquitetura Obrigatória

```
projeto/
├── src/
│   ├── domain/
│   ├── application/
│   ├── infrastructure/
│   ├── presentation/
│   └── shared/
├── tests/
├── docs/
├── public/
└── config/
```

Camadas: Domain → Application → Infrastructure → Presentation  
Dependência sempre aponta para dentro.

---

## 8. Performance (Obrigatório)

- LCP < 2.5s
- CLS < 0.1
- INP < 200ms
- Usar next/image (ou equivalente)
- Skeleton loaders obrigatórios
- Code splitting inteligente

---

## 9. Testes (Obrigatório)

- Unitários: Domain e Application
- Integração: APIs
- E2E: fluxos críticos (Playwright ou Cypress)
- Cobertura mínima recomendada: 80% nas camadas Domain e Application

---

## 10. Segurança (Reforçada)

### 10.1 Autenticação
- argon2/bcrypt
- Política de senhas forte
- Refresh Token Rotation
- Session + Idle timeout
- Proteção contra brute force
- MFA obrigatório no `/dashboard`

### 10.2 Autorização
- RBAC + menor privilégio
- Separação clara Admin × Usuário

### 10.3 Proteção
- Validação rigorosa no backend
- Headers de segurança obrigatórios (CSP, HSTS, X-Frame-Options, etc.)
- Secrets nunca no código
- Rate limiting
- Criptografia em trânsito e em repouso (dados sensíveis)
- Logs de auditoria imutáveis (especialmente no `/dashboard`)

---

## 11. Zero-Tolerância

Proibido:
- Serviços incompletos
- Gambiarras
- Interfaces genéricas
- Confundir `/dashboard` com área do usuário
- Avançar sem a fase anterior estar 100% pronta
- Usar bibliotecas de UI proibidas

**Done Means Done** só quando:
1. Backend testado
2. Frontend consumindo API real
3. Todos os estados tratados
4. Testado de ponta a ponta
5. Segurança validada
6. Performance dentro dos targets
7. UI no padrão Unicórnio

---

## 12. Regras para SaaS e Painel Administrativo

- HTTPS + MFA + RBAC
- Logs de auditoria imutáveis
- Nenhuma rota administrativa pública
- Conformidade LGPD
- Isolamento rigoroso em multi-tenancy

---

## 13. Checklist Final do Agente

### Planejamento
- [ ] Documento lido
- [ ] Prompt Mestre de Geração UI preenchido
- [ ] Design System definido
- [ ] Arquitetura definida

### UI/UX
- [ ] Design System formal com tokens
- [ ] Componentes personalizados (não visual padrão)
- [ ] Todos os estados implementados
- [ ] Acessibilidade e responsividade
- [ ] Interface não genérica

### Separação de Áreas
- [ ] `/dashboard` é exclusivamente o Painel Admin
- [ ] Área do usuário usa rota própria

### Backend, Segurança e Testes
- [ ] Autenticação e autorização sólidas
- [ ] MFA no painel admin
- [ ] Headers de segurança
- [ ] Testes unitários, integração e E2E
- [ ] Secrets protegidos

### Performance e Qualidade
- [ ] Core Web Vitals dentro dos targets
- [ ] Skeleton loaders
- [ ] Zero gambiarra
- [ ] Padrão Unicórnio atingido

---

## 14. Evolução deste Documento

Este documento é uma **fonte viva**.  
Deve ser atualizado sempre que melhores práticas surgirem ou o padrão Unicórnio evoluir.

---

**Fim do Documento**  
**Padrão: Unicórnio**  
**Versão 1.7 – Prompt Mestre de Geração UI Integrado + Checklist Passo a Passo + Segurança + Testes + Design System + Performance**  
**Uso obrigatório por todos os agentes de IA.**

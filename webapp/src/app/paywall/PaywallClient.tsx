'use client'

import { motion } from 'framer-motion'
import { useState } from 'react'
import { useRouter } from 'next/navigation'

interface PaywallClientProps {
  userId: string
  userEmail: string
  userName: string
}

type PaywallState = 'idle' | 'loading' | 'error'

const features = [
  { icon: 'receipt_long', label: 'Lançamentos ilimitados de receitas e despesas' },
  { icon: 'pie_chart', label: 'Gráficos e relatórios mensais detalhados' },
  { icon: 'account_balance_wallet', label: 'Controle de metas e orçamento mensal' },
  { icon: 'sync', label: 'Acesso em todos os dispositivos' },
  { icon: 'lock', label: 'Dados 100% privados e seguros' },
]

export default function PaywallClient({ userId, userEmail, userName }: PaywallClientProps) {
  const router = useRouter()
  const [state, setState] = useState<PaywallState>('idle')
  const [errorMsg, setErrorMsg] = useState('')

  async function handleAssinar() {
    setState('loading')
    setErrorMsg('')

    try {
      const res = await fetch('/api/create-checkout', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userId, userEmail, userName }),
      })

      const data = await res.json()

      if (!res.ok) {
        setErrorMsg(data.error || 'Não foi possível gerar o link de pagamento.')
        setState('error')
        return
      }

      // Redireciona para o checkout do Mercado Pago
      window.location.href = data.checkoutUrl
    } catch {
      setErrorMsg('Falha de conexão. Verifique sua internet e tente novamente.')
      setState('error')
    }
  }

  return (
    <main className="min-h-screen bg-background flex flex-col items-center justify-center p-6">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="w-full max-w-sm"
      >
        {/* Ícone */}
        <div className="flex flex-col items-center mb-6">
          <div className="w-20 h-20 bg-amber-100 dark:bg-amber-500/20 rounded-full flex items-center justify-center mb-4">
            <span className="material-symbols-outlined text-4xl text-amber-500">lock</span>
          </div>
          <h1 className="text-2xl font-black text-foreground text-center">Desbloqueie o Meu DinDin</h1>
          <p className="text-sm text-muted-foreground text-center mt-2 leading-relaxed">
            Seu acesso expirou ou ainda não foi confirmado. Assine agora e volte a controlar suas finanças.
          </p>
        </div>

        {/* Card de plano */}
        <div className="bg-card border border-border rounded-3xl overflow-hidden mb-4">
          {/* Topo destacado */}
          <div className="bg-primary px-6 py-4 text-primary-foreground text-center">
            <p className="text-xs font-semibold uppercase tracking-widest opacity-80 mb-1">Plano Anual</p>
            <div className="flex items-baseline justify-center gap-1">
              <span className="text-4xl font-black">R$ 29</span>
              <span className="text-base font-medium opacity-80">/ano</span>
            </div>
            <p className="text-xs opacity-70 mt-1 font-medium">Menos de R$ 2,50 por mês</p>
          </div>

          {/* Features */}
          <div className="px-6 py-5 flex flex-col gap-3">
            {features.map((f) => (
              <div key={f.icon} className="flex items-center gap-3">
                <div className="w-7 h-7 rounded-lg bg-primary/10 flex items-center justify-center shrink-0">
                  <span className="material-symbols-outlined text-primary text-sm">{f.icon}</span>
                </div>
                <span className="text-sm font-medium text-foreground/80">{f.label}</span>
              </div>
            ))}
          </div>
        </div>

        {/* Erro */}
        {state === 'error' && errorMsg && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            className="flex items-center gap-2 bg-destructive/10 border border-destructive/20 text-destructive rounded-xl px-4 py-3 mb-4"
          >
            <span className="material-symbols-outlined text-base shrink-0">error</span>
            <p className="text-sm font-semibold">{errorMsg}</p>
          </motion.div>
        )}

        {/* Botão Assinar */}
        <button
          onClick={handleAssinar}
          disabled={state === 'loading'}
          className="w-full bg-primary text-primary-foreground py-4 rounded-xl font-bold text-base hover:scale-[1.02] active:scale-[0.98] transition-transform disabled:opacity-60 disabled:pointer-events-none shadow-[0_8px_30px_rgb(0,105,72,0.25)] flex items-center justify-center gap-2 mb-3"
        >
          {state === 'loading' ? (
            <>
              <span className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
              Gerando link...
            </>
          ) : (
            <>
              <span className="material-symbols-outlined text-lg">credit_card</span>
              Assinar Agora
            </>
          )}
        </button>

        {/* Segurança */}
        <div className="flex items-center justify-center gap-2 text-muted-foreground">
          <span className="material-symbols-outlined text-sm text-[#1db576]">lock</span>
          <p className="text-xs font-medium">Pagamento seguro · Cancele quando quiser</p>
        </div>
      </motion.div>
    </main>
  )
}

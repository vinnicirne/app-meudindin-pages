'use client'

import * as motion from 'framer-motion/client'
import { useState } from 'react'
import { useRouter } from 'next/navigation'
import Link from 'next/link'

type FormState = 'idle' | 'loading' | 'success' | 'error'

export default function CadastroPage() {
  const router = useRouter()
  const [formState, setFormState] = useState<FormState>('idle')
  const [errorMsg, setErrorMsg] = useState('')
  const [showPassword, setShowPassword] = useState(false)

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault()
    setErrorMsg('')
    setFormState('loading')

    const form = e.currentTarget
    const name = (form.elements.namedItem('name') as HTMLInputElement).value.trim()
    const email = (form.elements.namedItem('email') as HTMLInputElement).value.trim()
    const password = (form.elements.namedItem('password') as HTMLInputElement).value

    try {
      const res = await fetch('/api/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, email, password }),
      })

      const data = await res.json()

      if (!res.ok) {
        setErrorMsg(data.error || 'Erro ao criar conta. Tente novamente.')
        setFormState('error')
        return
      }

      setFormState('success')

      // Redireciona para o checkout do Mercado Pago
      // Usa window.location para garantir navegação externa
      window.location.href = data.checkoutUrl

    } catch {
      setErrorMsg('Falha de conexão. Verifique sua internet e tente novamente.')
      setFormState('error')
    }
  }

  const isLoading = formState === 'loading' || formState === 'success'

  return (
    <main className="min-h-screen bg-background flex flex-col items-center justify-center p-6 relative overflow-hidden">
      {/* Background decorator */}
      <div className="absolute top-0 left-0 w-full h-1/3 bg-primary/10 -skew-y-6 transform origin-top-left -z-10" />

      <motion.div
        initial={{ opacity: 0, y: 30 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.4 }}
        className="w-full max-w-sm"
      >
        {/* Header */}
        <div className="flex flex-col items-center mb-8 gap-3">
          <motion.div
            initial={{ scale: 0.8, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            transition={{ delay: 0.1 }}
            className="w-14 h-14 bg-primary rounded-2xl flex items-center justify-center text-primary-foreground font-bold text-3xl shadow-lg shadow-primary/30"
          >
            $
          </motion.div>
          <h1 className="text-2xl font-extrabold tracking-tight text-foreground">Crie sua conta</h1>
          <p className="text-sm font-medium text-muted-foreground text-center">
            Preencha os dados abaixo e em seguida você será levado ao pagamento.
          </p>
        </div>

        {/* Card */}
        <div className="bg-card p-8 rounded-[2rem] shadow-[0_8px_30px_rgb(0,0,0,0.06)] border border-border">
          {/* Progresso do funil */}
          <div className="flex items-center gap-2 mb-6">
            <div className="flex items-center gap-1.5">
              <div className="w-6 h-6 rounded-full bg-primary flex items-center justify-center text-primary-foreground text-xs font-bold">1</div>
              <span className="text-xs font-semibold text-primary">Cadastro</span>
            </div>
            <div className="flex-1 h-px bg-border" />
            <div className="flex items-center gap-1.5">
              <div className="w-6 h-6 rounded-full bg-muted flex items-center justify-center text-muted-foreground text-xs font-bold">2</div>
              <span className="text-xs font-medium text-muted-foreground">Pagamento</span>
            </div>
            <div className="flex-1 h-px bg-border" />
            <div className="flex items-center gap-1.5">
              <div className="w-6 h-6 rounded-full bg-muted flex items-center justify-center text-muted-foreground text-xs font-bold">3</div>
              <span className="text-xs font-medium text-muted-foreground">Acesso</span>
            </div>
          </div>

          <form onSubmit={handleSubmit} className="flex flex-col gap-4">
            {/* Nome */}
            <div className="flex flex-col gap-1.5">
              <label htmlFor="name" className="text-sm font-semibold text-foreground/80">
                Nome completo
              </label>
              <input
                id="name"
                name="name"
                type="text"
                required
                autoComplete="name"
                placeholder="João Silva"
                disabled={isLoading}
                className="w-full bg-muted/50 border border-border rounded-xl px-4 py-3 font-medium text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50 transition-shadow disabled:opacity-60"
              />
            </div>

            {/* Email */}
            <div className="flex flex-col gap-1.5">
              <label htmlFor="email" className="text-sm font-semibold text-foreground/80">
                E-mail
              </label>
              <input
                id="email"
                name="email"
                type="email"
                required
                autoComplete="email"
                placeholder="seu@email.com"
                disabled={isLoading}
                className="w-full bg-muted/50 border border-border rounded-xl px-4 py-3 font-medium text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50 transition-shadow disabled:opacity-60"
              />
            </div>

            {/* Senha */}
            <div className="flex flex-col gap-1.5">
              <label htmlFor="password" className="text-sm font-semibold text-foreground/80">
                Senha
              </label>
              <div className="relative">
                <input
                  id="password"
                  name="password"
                  type={showPassword ? 'text' : 'password'}
                  required
                  minLength={6}
                  autoComplete="new-password"
                  placeholder="Mínimo 6 caracteres"
                  disabled={isLoading}
                  className="w-full bg-muted/50 border border-border rounded-xl px-4 py-3 pr-12 font-medium text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50 transition-shadow disabled:opacity-60"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(v => !v)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 p-1 text-muted-foreground hover:text-foreground transition-colors"
                  title={showPassword ? 'Ocultar senha' : 'Mostrar senha'}
                >
                  <span className="material-symbols-outlined text-xl">
                    {showPassword ? 'visibility_off' : 'visibility'}
                  </span>
                </button>
              </div>
            </div>

            {/* Erro */}
            {formState === 'error' && errorMsg && (
              <motion.div
                initial={{ opacity: 0, y: -8 }}
                animate={{ opacity: 1, y: 0 }}
                className="flex items-center gap-2 bg-destructive/10 border border-destructive/20 text-destructive rounded-xl px-4 py-3"
              >
                <span className="material-symbols-outlined text-base shrink-0">error</span>
                <p className="text-sm font-semibold">{errorMsg}</p>
              </motion.div>
            )}

            {/* Botão principal */}
            <button
              type="submit"
              disabled={isLoading}
              className="mt-2 w-full bg-primary text-primary-foreground py-4 rounded-xl font-bold hover:scale-[1.02] active:scale-[0.98] transition-transform disabled:opacity-60 disabled:pointer-events-none shadow-[0_8px_30px_rgb(0,105,72,0.2)] flex items-center justify-center gap-2"
            >
              {isLoading ? (
                <>
                  <span className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                  {formState === 'success' ? 'Redirecionando...' : 'Criando conta...'}
                </>
              ) : (
                <>
                  <span>Continuar para o Pagamento</span>
                  <span className="material-symbols-outlined text-lg">arrow_forward</span>
                </>
              )}
            </button>
          </form>
        </div>

        {/* Segurança */}
        <div className="flex items-center justify-center gap-2 mt-5 text-muted-foreground">
          <span className="material-symbols-outlined text-sm text-[#1db576]">lock</span>
          <p className="text-xs font-medium">Pagamento 100% seguro via Mercado Pago</p>
        </div>

        {/* Link para login */}
        <p className="text-center text-sm text-muted-foreground mt-4">
          Já tem conta?{' '}
          <Link href="/login" className="font-bold text-primary hover:underline">
            Fazer login
          </Link>
        </p>
      </motion.div>
    </main>
  )
}

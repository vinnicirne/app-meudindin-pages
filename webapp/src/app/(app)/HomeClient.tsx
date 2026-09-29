'use client'

import * as motion from "framer-motion/client"
import Link from 'next/link'
import { useState, useMemo } from 'react'

interface Transaction {
  id: string
  amount: number
  description: string
  date: string
  type: 'INCOME' | 'EXPENSE'
  category_id: string
}

const MONTH_NAMES = [
  'Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
  'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'
]

function formatCurrency(value: number) {
  return value.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
}

export default function HomeClient({ transactions }: { transactions: Transaction[] }) {
  const today = new Date()
  const [year, setYear] = useState(today.getFullYear())
  const [month, setMonth] = useState(today.getMonth())

  function prevMonth() {
    if (month === 0) { setMonth(11); setYear(y => y - 1) }
    else setMonth(m => m - 1)
  }

  function nextMonth() {
    if (month === 11) { setMonth(0); setYear(y => y + 1) }
    else setMonth(m => m + 1)
  }

  const filtered = useMemo(() =>
    transactions.filter(t => {
      const d = new Date(t.date)
      return d.getFullYear() === year && d.getMonth() === month
    }),
    [transactions, year, month]
  )

  const totalIncome = useMemo(() =>
    filtered.filter(t => t.type === 'INCOME').reduce((s, t) => s + t.amount, 0),
    [filtered]
  )
  const totalExpense = useMemo(() =>
    filtered.filter(t => t.type === 'EXPENSE').reduce((s, t) => s + t.amount, 0),
    [filtered]
  )
  const balance = totalIncome - totalExpense

  const categoryLabel: Record<string, string> = {
    alimentacao: 'Alimentação',
    transporte: 'Transporte',
    moradia: 'Moradia',
    salario: 'Salário',
    lazer: 'Lazer',
    outros: 'Outros',
  }

  return (
    <main className="flex-1 flex flex-col p-4 max-w-md mx-auto w-full relative min-h-screen pb-24">

      {/* Month Selector */}
      <motion.div
        initial={{ opacity: 0, y: -10 }}
        animate={{ opacity: 1, y: 0 }}
        className="flex items-center justify-between mb-4 mt-2 px-4"
      >
        <button
          onClick={prevMonth}
          className="w-8 h-8 flex items-center justify-center text-foreground hover:bg-muted rounded-full"
        >
          <span className="material-symbols-outlined text-lg">arrow_back_ios_new</span>
        </button>
        <h1 className="text-lg font-bold text-foreground">
          {MONTH_NAMES[month]} {year}
        </h1>
        <button
          onClick={nextMonth}
          className="w-8 h-8 flex items-center justify-center text-foreground hover:bg-muted rounded-full"
        >
          <span className="material-symbols-outlined text-lg">arrow_forward_ios</span>
        </button>
      </motion.div>

      {/* Saldo Principal */}
      <motion.div
        initial={{ opacity: 0, scale: 0.95 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ delay: 0.1 }}
        className="bg-[#1a5b48] text-white rounded-3xl p-5 shadow-sm mb-4"
      >
        <div className="flex flex-col gap-1 mb-4">
          <span className="text-white/80 text-xs font-semibold">Saldo Total Geral</span>
          <span className="text-3xl font-extrabold tracking-tight">{formatCurrency(balance)}</span>
        </div>
        <div className="mb-6">
          <div className="inline-flex bg-[#23735b] px-3 py-1.5 rounded-full items-center gap-1">
            <span className="text-[10px] font-semibold text-white/90">
              Resultado no mês: {balance >= 0 ? '+' : ''}{formatCurrency(balance)}
            </span>
          </div>
        </div>

        <div className="flex gap-3">
          <Link href="/add?type=INCOME" className="flex-1 bg-[#1db576] hover:bg-[#1db576]/90 text-white font-bold py-3 rounded-xl flex items-center justify-center gap-2 transition-colors">
            <span className="material-symbols-outlined text-lg">add</span>
            <span className="text-sm">Receita</span>
          </Link>
          <Link href="/add?type=EXPENSE" className="flex-1 bg-[#e74c4c] hover:bg-[#e74c4c]/90 text-white font-bold py-3 rounded-xl flex items-center justify-center gap-2 transition-colors">
            <span className="material-symbols-outlined text-lg">remove</span>
            <span className="text-sm">Despesa</span>
          </Link>
        </div>
      </motion.div>

      {/* Resumo Mês */}
      <motion.div
        initial={{ opacity: 0, y: 10 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.2 }}
        className="grid grid-cols-2 gap-3 mb-4"
      >
        <div className="bg-card p-4 rounded-2xl border border-border shadow-sm flex flex-col gap-2">
          <div className="flex items-center gap-2 text-foreground/70">
            <div className="w-6 h-6 rounded-full bg-[#1db576]/10 flex items-center justify-center text-[#1db576]">
              <span className="material-symbols-outlined text-[14px]">arrow_upward</span>
            </div>
            <span className="text-xs font-semibold">Receitas</span>
          </div>
          <div>
            <p className="text-[#1db576] font-bold text-lg">{formatCurrency(totalIncome)}</p>
            <p className="text-[9px] text-muted-foreground mt-0.5 font-medium">No mês selecionado</p>
          </div>
        </div>

        <div className="bg-card p-4 rounded-2xl border border-border shadow-sm flex flex-col gap-2">
          <div className="flex items-center gap-2 text-foreground/70">
            <div className="w-6 h-6 rounded-full bg-[#e74c4c]/10 flex items-center justify-center text-[#e74c4c]">
              <span className="material-symbols-outlined text-[14px]">arrow_downward</span>
            </div>
            <span className="text-xs font-semibold">Despesas</span>
          </div>
          <div>
            <p className="text-[#e74c4c] font-bold text-lg">{formatCurrency(totalExpense)}</p>
            <p className="text-[9px] text-muted-foreground mt-0.5 font-medium">No mês selecionado</p>
          </div>
        </div>
      </motion.div>

      {/* Orçamento & Metas */}
      <motion.div
        initial={{ opacity: 0, y: 10 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.3 }}
        className="bg-card rounded-2xl p-4 border border-border shadow-sm mb-6 flex flex-col justify-center"
      >
        <div className="flex justify-between items-center mb-2">
          <div className="flex items-center gap-2">
            <span className="material-symbols-outlined text-[#1a5b48] text-xl">receipt_long</span>
            <h3 className="font-bold text-sm text-foreground">Orçamento & Metas</h3>
          </div>
          <Link href="/planning" className="text-xs font-bold text-[#1a5b48] hover:underline">Definir limites</Link>
        </div>
        <p className="text-xs text-muted-foreground leading-relaxed pr-4 font-medium">
          Defina limites mensais de gastos para suas categorias e acompanhe o progresso em tempo real.
        </p>
      </motion.div>

      {/* Transações */}
      <motion.div
        initial={{ opacity: 0, y: 10 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.4 }}
        className="flex flex-col flex-1"
      >
        <h2 className="text-lg font-bold text-foreground mb-3 px-1">Transações do Mês</h2>

        {filtered.length === 0 ? (
          <div className="bg-card rounded-2xl p-8 border border-border shadow-sm flex flex-col items-center justify-center text-center gap-3 flex-1 min-h-[200px]">
            <div className="w-12 h-12 rounded-full bg-muted flex items-center justify-center text-[#1a5b48] mb-2">
              <span className="material-symbols-outlined">receipt_long</span>
            </div>
            <h3 className="font-bold text-sm text-foreground">Nenhuma movimentação neste mês</h3>
            <p className="text-xs text-muted-foreground max-w-[250px] font-medium leading-relaxed">
              Toque em + Receita ou + Despesa para registrar suas finanças.
            </p>
          </div>
        ) : (
          <div className="flex flex-col gap-2">
            {filtered.map((t) => (
              <div key={t.id} className="bg-card rounded-2xl p-4 border border-border shadow-sm flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <div className={`w-10 h-10 rounded-full flex items-center justify-center ${
                    t.type === 'INCOME' ? 'bg-[#1db576]/10 text-[#1db576]' : 'bg-[#e74c4c]/10 text-[#e74c4c]'
                  }`}>
                    <span className="material-symbols-outlined text-[18px]">
                      {t.type === 'INCOME' ? 'arrow_upward' : 'arrow_downward'}
                    </span>
                  </div>
                  <div>
                    <p className="text-sm font-semibold text-foreground">{t.description}</p>
                    <p className="text-[10px] text-muted-foreground">{categoryLabel[t.category_id] || t.category_id} · {new Date(t.date).toLocaleDateString('pt-BR')}</p>
                  </div>
                </div>
                <span className={`font-bold text-sm ${t.type === 'INCOME' ? 'text-[#1db576]' : 'text-[#e74c4c]'}`}>
                  {t.type === 'INCOME' ? '+' : '-'}{formatCurrency(t.amount)}
                </span>
              </div>
            ))}
          </div>
        )}
      </motion.div>
    </main>
  )
}

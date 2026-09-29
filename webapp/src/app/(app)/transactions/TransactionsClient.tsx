'use client'

import * as motion from "framer-motion/client"
import { useState, useMemo } from "react"

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

const categoryLabel: Record<string, string> = {
  alimentacao: 'Alimentação',
  transporte: 'Transporte',
  moradia: 'Moradia',
  salario: 'Salário',
  lazer: 'Lazer',
  outros: 'Outros',
}

const CATEGORY_COLORS: Record<string, string> = {
  alimentacao: 'bg-orange-500',
  transporte: 'bg-blue-500',
  moradia: 'bg-purple-500',
  salario: 'bg-green-500',
  lazer: 'bg-pink-500',
  outros: 'bg-gray-400',
}

export default function TransactionsClient({ transactions }: { transactions: Transaction[] }) {
  const today = new Date()
  const [year, setYear] = useState(today.getFullYear())
  const [month, setMonth] = useState(today.getMonth())
  const [activeType, setActiveType] = useState('Todas')
  const [activeCategory, setActiveCategory] = useState('todas')
  const [search, setSearch] = useState('')

  function prevMonth() {
    if (month === 0) { setMonth(11); setYear(y => y - 1) }
    else setMonth(m => m - 1)
  }

  function nextMonth() {
    if (month === 11) { setMonth(0); setYear(y => y + 1) }
    else setMonth(m => m + 1)
  }

  const byMonth = useMemo(() =>
    transactions.filter(t => {
      const d = new Date(t.date)
      return d.getFullYear() === year && d.getMonth() === month
    }), [transactions, year, month])

  const filtered = useMemo(() => {
    return byMonth.filter(t => {
      if (activeType === 'Receitas' && t.type !== 'INCOME') return false
      if (activeType === 'Despesas' && t.type !== 'EXPENSE') return false
      if (activeCategory !== 'todas' && t.category_id !== activeCategory) return false
      if (search && !t.description.toLowerCase().includes(search.toLowerCase())) return false
      return true
    })
  }, [byMonth, activeType, activeCategory, search])

  const totalIncome = filtered.filter(t => t.type === 'INCOME').reduce((s, t) => s + t.amount, 0)
  const totalExpense = filtered.filter(t => t.type === 'EXPENSE').reduce((s, t) => s + t.amount, 0)
  const liquid = totalIncome - totalExpense

  const categories = useMemo(() => {
    const seen = new Set(byMonth.map(t => t.category_id))
    return ['todas', ...Array.from(seen)]
  }, [byMonth])

  const types = ['Todas', 'Receitas', 'Despesas']

  return (
    <main className="flex-1 flex flex-col p-4 max-w-md mx-auto w-full relative min-h-screen pb-24">

      {/* Month Selector */}
      <div className="bg-card rounded-2xl p-2 mb-4 shadow-sm border border-border/50">
        <div className="flex items-center justify-between px-2 py-1">
          <button onClick={prevMonth} className="w-8 h-8 flex items-center justify-center text-foreground hover:bg-muted rounded-full">
            <span className="material-symbols-outlined text-sm">arrow_back_ios_new</span>
          </button>
          <h1 className="text-sm font-bold text-foreground">{MONTH_NAMES[month]} {year}</h1>
          <button onClick={nextMonth} className="w-8 h-8 flex items-center justify-center text-foreground hover:bg-muted rounded-full">
            <span className="material-symbols-outlined text-sm">arrow_forward_ios</span>
          </button>
        </div>
      </div>

      {/* Search Input */}
      <div className="relative mb-4">
        <span className="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground text-xl">search</span>
        <input
          type="text"
          placeholder="Pesquisar por descrição..."
          value={search}
          onChange={e => setSearch(e.target.value)}
          className="w-full bg-transparent border border-border rounded-xl pl-10 pr-4 py-3 text-sm text-foreground focus:outline-none focus:border-primary/50"
        />
      </div>

      {/* Type Filters */}
      <div className="flex gap-2 mb-4 overflow-x-auto pb-1">
        {types.map((type) => (
          <button
            key={type}
            onClick={() => setActiveType(type)}
            className={`px-4 py-1.5 rounded-lg text-xs font-semibold whitespace-nowrap transition-colors ${
              activeType === type
                ? 'bg-primary/10 text-primary border border-transparent'
                : 'bg-transparent text-foreground/80 border border-border hover:bg-muted'
            }`}
          >
            {type}
          </button>
        ))}
      </div>

      {/* Category Filters */}
      <div className="flex gap-2 mb-4 overflow-x-auto pb-1">
        {categories.map((cat) => (
          <button
            key={cat}
            onClick={() => setActiveCategory(cat)}
            className={`px-3 py-1 rounded-full text-[10px] font-medium whitespace-nowrap flex items-center gap-1 transition-colors ${
              activeCategory === cat
                ? 'bg-primary/10 text-primary border border-transparent font-bold'
                : 'bg-transparent text-foreground/80 border border-transparent hover:bg-muted'
            }`}
          >
            {cat !== 'todas' && <span className={`w-2 h-2 rounded-full ${CATEGORY_COLORS[cat] || 'bg-gray-400'}`} />}
            {cat === 'todas' ? 'Todas Categorias' : (categoryLabel[cat] || cat)}
          </button>
        ))}
      </div>

      {/* Summary Row */}
      <div className="flex justify-between items-center mb-4 px-1">
        <span className="text-[10px] text-muted-foreground font-medium">{filtered.length} registro(s)</span>
        <span className={`text-[10px] font-bold ${liquid >= 0 ? 'text-[#1db576]' : 'text-[#e74c4c]'}`}>
          Líquido: {formatCurrency(liquid)}
        </span>
      </div>

      {/* Transactions List */}
      {filtered.length === 0 ? (
        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          className="flex flex-col items-center justify-center text-center flex-1"
        >
          <span className="material-symbols-outlined text-[48px] text-muted-foreground/40 mb-4">filter_list</span>
          <h3 className="font-bold text-sm text-foreground mb-1">Nenhuma transação encontrada</h3>
          <p className="text-xs text-muted-foreground">Tente alterar os filtros ou o mês selecionado</p>
        </motion.div>
      ) : (
        <div className="flex flex-col gap-2">
          {filtered.map((t) => (
            <motion.div
              key={t.id}
              initial={{ opacity: 0, y: 5 }}
              animate={{ opacity: 1, y: 0 }}
              className="bg-card rounded-2xl p-4 border border-border shadow-sm flex items-center justify-between"
            >
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
                  <p className="text-[10px] text-muted-foreground">
                    {categoryLabel[t.category_id] || t.category_id} · {new Date(t.date).toLocaleDateString('pt-BR')}
                  </p>
                </div>
              </div>
              <span className={`font-bold text-sm ${t.type === 'INCOME' ? 'text-[#1db576]' : 'text-[#e74c4c]'}`}>
                {t.type === 'INCOME' ? '+' : '-'}{formatCurrency(t.amount)}
              </span>
            </motion.div>
          ))}
        </div>
      )}
    </main>
  )
}

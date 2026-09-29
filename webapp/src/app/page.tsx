import * as motion from "framer-motion/client";
import Link from 'next/link';

export default function Home() {
  return (
    <main className="flex-1 flex flex-col p-4 max-w-md mx-auto w-full relative bg-[#f8f9ff] min-h-screen pb-24">
      {/* Month Selector */}
      <motion.div 
        initial={{ opacity: 0, y: -10 }}
        animate={{ opacity: 1, y: 0 }}
        className="flex items-center justify-between mb-4 mt-2 px-4"
      >
        <button className="w-8 h-8 flex items-center justify-center text-foreground hover:bg-muted rounded-full">
          <span className="material-symbols-outlined text-lg">arrow_back_ios_new</span>
        </button>
        <h1 className="text-lg font-bold text-foreground">
          Setembro 2026
        </h1>
        <button className="w-8 h-8 flex items-center justify-center text-foreground hover:bg-muted rounded-full">
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
          <span className="text-3xl font-extrabold tracking-tight">R$ 0,00</span>
        </div>
        <div className="mb-6">
          <div className="inline-flex bg-[#23735b] px-3 py-1.5 rounded-full items-center gap-1">
            <span className="text-[10px] font-semibold text-white/90">Resultado no mês: + R$ 0,00</span>
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

      {/* Resumo Mes */}
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
            <p className="text-[#1db576] font-bold text-lg">R$ 0,00</p>
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
            <p className="text-[#e74c4c] font-bold text-lg">R$ 0,00</p>
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
        <h2 className="text-lg font-bold text-foreground mb-3 px-1">Transações</h2>
        
        <div className="bg-card rounded-2xl p-8 border border-border shadow-sm flex flex-col items-center justify-center text-center gap-3 flex-1 min-h-[200px]">
          <div className="w-12 h-12 rounded-full bg-[#f1f5f3] flex items-center justify-center text-[#1a5b48] mb-2">
            <span className="material-symbols-outlined">receipt_long</span>
          </div>
          <h3 className="font-bold text-sm text-foreground">Nenhuma movimentação neste mês</h3>
          <p className="text-xs text-muted-foreground max-w-[250px] font-medium leading-relaxed">
            Toque em + Receita ou + Despesa para registrar suas finanças.
          </p>
        </div>
      </motion.div>
    </main>
  );
}

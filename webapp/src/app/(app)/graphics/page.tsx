import * as motion from "framer-motion/client";

export default function GraphicsPage() {
  return (
    <main className="flex-1 flex flex-col p-4 max-w-md mx-auto w-full relative bg-background min-h-screen pb-24">
      
      {/* Month Selector */}
      <div className="bg-card rounded-2xl p-2 mb-4 shadow-sm border border-border/50">
        <div className="flex items-center justify-between px-2 py-1">
          <button className="w-8 h-8 flex items-center justify-center text-foreground hover:bg-muted rounded-full">
            <span className="material-symbols-outlined text-sm">arrow_back_ios_new</span>
          </button>
          <h1 className="text-sm font-bold text-foreground">
            Setembro 2026
          </h1>
          <button className="w-8 h-8 flex items-center justify-center text-foreground hover:bg-muted rounded-full">
            <span className="material-symbols-outlined text-sm">arrow_forward_ios</span>
          </button>
        </div>
      </div>

      {/* Resumo Mensal do Cotidiano */}
      <motion.div 
        initial={{ opacity: 0, y: 10 }}
        animate={{ opacity: 1, y: 0 }}
        className="bg-card rounded-2xl p-4 mb-4 shadow-sm border border-border/50"
      >
        <h2 className="font-bold text-sm text-foreground mb-4">Resumo Mensal do Cotidiano</h2>
        
        <div className="grid grid-cols-3 gap-2">
          <div className="flex flex-col gap-1">
            <div className="flex items-center gap-1 text-muted-foreground">
              <span className="material-symbols-outlined text-[14px]">calendar_today</span>
              <span className="text-[10px] font-medium">Média diária</span>
            </div>
            <p className="font-bold text-foreground text-sm">R$ 0,00</p>
          </div>
          
          <div className="flex flex-col gap-1">
            <div className="flex items-center gap-1 text-muted-foreground">
              <span className="material-symbols-outlined text-[14px]">trending_up</span>
              <span className="text-[10px] font-medium">Taxa economia</span>
            </div>
            <p className="font-bold text-[#1db576] text-sm">0.0%</p>
          </div>
          
          <div className="flex flex-col gap-1">
            <div className="flex items-center gap-1 text-muted-foreground">
              <span className="material-symbols-outlined text-[14px]">swap_horiz</span>
              <span className="text-[10px] font-medium">Saldo do mês</span>
            </div>
            <p className="font-bold text-[#1db576] text-sm">R$ 0,00</p>
          </div>
        </div>
      </motion.div>

      {/* Banner de Dica */}
      <motion.div 
        initial={{ opacity: 0, y: 10 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.1 }}
        className="bg-[#e4fcf1] rounded-2xl p-4 mb-4 flex items-center gap-3"
      >
        <div className="w-10 h-10 rounded-full bg-[#1a5b48] flex flex-shrink-0 items-center justify-center text-white">
          <span className="material-symbols-outlined text-xl">lightbulb</span>
        </div>
        <div>
          <h3 className="text-[11px] font-bold text-[#1a5b48] mb-0.5">Dica de Gestão Financeira</h3>
          <p className="text-[10px] text-[#1a5b48]/80 leading-snug font-medium">
            Adicione suas receitas e despesas para acompanhar gráficos e obter dicas personalizadas.
          </p>
        </div>
      </motion.div>

      {/* Despesas por Categoria (Donut Chart Placeholder) */}
      <motion.div 
        initial={{ opacity: 0, y: 10 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.2 }}
        className="bg-card rounded-2xl p-4 mb-4 shadow-sm border border-border/50"
      >
        <div className="flex justify-between items-center mb-6">
          <h2 className="font-bold text-sm text-foreground">Despesas por Categoria</h2>
          <span className="text-[10px] text-muted-foreground font-medium">0 categorias</span>
        </div>
        
        <div className="flex justify-center items-center py-6">
          <div className="w-40 h-40 rounded-full border-[20px] border-muted/50 flex flex-col items-center justify-center text-center">
            <span className="font-bold text-[11px] text-foreground">Sem gastos</span>
            <span className="text-[10px] text-muted-foreground font-medium">no período</span>
          </div>
        </div>
      </motion.div>

      {/* Evolução Mensal (Line Chart Placeholder) */}
      <motion.div 
        initial={{ opacity: 0, y: 10 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.3 }}
        className="bg-card rounded-2xl p-4 shadow-sm border border-border/50 min-h-[200px]"
      >
        <div className="flex justify-between items-center mb-6">
          <h2 className="font-bold text-sm text-foreground">Evolução Mensal (6 Meses)</h2>
          <div className="flex gap-3">
            <div className="flex items-center gap-1">
              <span className="w-1.5 h-1.5 rounded-full bg-[#1db576]"></span>
              <span className="text-[9px] font-medium text-foreground">Receitas</span>
            </div>
            <div className="flex items-center gap-1">
              <span className="w-1.5 h-1.5 rounded-full bg-[#e74c4c]"></span>
              <span className="text-[9px] font-medium text-foreground">Despesas</span>
            </div>
          </div>
        </div>
        
        {/* Gráfico vazio ficará aqui */}
        <div className="w-full h-full flex-1"></div>
      </motion.div>

    </main>
  );
}

'use client';

import * as motion from "framer-motion/client";
import { useState } from "react";

export default function PlanningPage() {
  const [activeFilter, setActiveFilter] = useState('Todas (11)');

  const filters = ['Todas (11)', 'Com Metas (0)', 'Sem Metas (11)'];

  const categories = [
    { name: 'Alimentação', icon: 'restaurant', color: 'bg-orange-100 text-orange-500' },
    { name: 'Supermercado', icon: 'shopping_cart', color: 'bg-green-100 text-green-500' },
    { name: 'Moradia', icon: 'home', color: 'bg-blue-100 text-blue-500' },
  ];

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

      {/* Orçamento Global Card */}
      <motion.div 
        initial={{ opacity: 0, scale: 0.95 }}
        animate={{ opacity: 1, scale: 1 }}
        className="bg-card rounded-2xl p-4 mb-4 shadow-sm border border-border/50"
      >
        <div className="flex justify-between items-start mb-4">
          <h2 className="font-bold text-sm text-foreground">Orçamento Global do Mês</h2>
          <span className="bg-[#e4fcf1] text-[#1db576] px-2 py-1 rounded text-[10px] font-bold">Sem metas</span>
        </div>

        <div className="flex justify-between mb-4">
          <div>
            <p className="text-[10px] text-muted-foreground font-medium">Gasto em categorias orçadas</p>
            <p className="text-xl font-extrabold text-foreground">R$ 0,00</p>
          </div>
          <div className="text-right">
            <p className="text-[10px] text-muted-foreground font-medium">Limite global planejado</p>
            <p className="text-sm font-bold text-[#1db576]">R$ 0,00</p>
          </div>
        </div>

        {/* Progress Bar */}
        <div className="relative h-1.5 bg-muted rounded-full mb-3">
          <div className="absolute right-0 top-1/2 -translate-y-1/2 w-1.5 h-1.5 rounded-full bg-[#1db576]"></div>
        </div>

        <div className="flex justify-between text-[10px] font-medium">
          <span className="text-foreground/70">Defina limites abaixo para acompanhar</span>
          <span className="text-foreground/70">0 categorias ativas</span>
        </div>
      </motion.div>

      {/* Filters */}
      <div className="flex gap-2 mb-6">
        {filters.map((filter) => (
          <button 
            key={filter}
            onClick={() => setActiveFilter(filter)}
            className={`px-4 py-1.5 rounded-lg text-xs font-semibold whitespace-nowrap transition-colors ${
              activeFilter === filter 
                ? 'bg-[#e4fcf1] text-[#1a5b48] border border-transparent' 
                : 'bg-transparent text-foreground/80 border border-border hover:bg-muted'
            }`}
          >
            {filter}
          </button>
        ))}
      </div>

      {/* Section Title */}
      <div className="flex justify-between items-center mb-4 px-1">
        <h3 className="font-bold text-sm text-foreground">Progresso por Categoria</h3>
        <span className="text-[10px] text-muted-foreground font-medium">Toque no lápis para alterar</span>
      </div>

      {/* Category Cards */}
      <div className="flex flex-col gap-3">
        {categories.map((cat, i) => (
          <motion.div 
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: i * 0.1 }}
            key={cat.name} 
            className="bg-card rounded-2xl p-4 shadow-sm border border-border/50"
          >
            <div className="flex justify-between items-start mb-4">
              <div className="flex items-center gap-3">
                <div className={`w-10 h-10 rounded-full flex items-center justify-center ${cat.color}`}>
                  <span className="material-symbols-outlined text-[18px]">{cat.icon}</span>
                </div>
                <div>
                  <h4 className="font-bold text-sm text-foreground">{cat.name}</h4>
                  <p className="text-xs text-muted-foreground">Sem meta definida</p>
                </div>
              </div>
              <button className="w-8 h-8 rounded-full hover:bg-muted flex items-center justify-center text-[#1a5b48]">
                <span className="material-symbols-outlined text-[18px]">edit</span>
              </button>
            </div>
            
            <div className="flex justify-between items-center">
              <span className="text-[10px] font-bold text-foreground">Total gasto: R$ 0,00</span>
              <span className="text-[10px] font-bold text-[#1db576]">Toque no lápis para criar meta</span>
            </div>
          </motion.div>
        ))}
      </div>

    </main>
  );
}

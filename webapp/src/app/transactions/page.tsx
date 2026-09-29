'use client';

import * as motion from "framer-motion/client";
import { useState } from "react";

export default function TransactionsPage() {
  const [activeType, setActiveType] = useState('Todas');
  const [activeCategory, setActiveCategory] = useState('Todas Categorias');

  const types = ['Todas', 'Receitas', 'Despesas', 'Parceladas (Cartão)'];
  const categories = [
    { name: 'Todas Categorias', color: '' },
    { name: 'Alimentação', color: 'bg-orange-500' },
    { name: 'Supermercado', color: 'bg-green-500' },
    { name: 'Moradia', color: 'bg-blue-500' },
  ];

  return (
    <main className="flex-1 flex flex-col p-4 max-w-md mx-auto w-full relative bg-[#f8f9ff] min-h-screen pb-24">
      
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

      {/* Search Input */}
      <div className="relative mb-4">
        <span className="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground text-xl">search</span>
        <input 
          type="text" 
          placeholder="Pesquisar por título, nota ou categoria..." 
          className="w-full bg-transparent border border-border rounded-xl pl-10 pr-4 py-3 text-sm text-foreground focus:outline-none focus:border-primary/50"
        />
      </div>

      {/* Type Filters */}
      <div className="flex gap-2 mb-4 overflow-x-auto pb-1 scrollbar-hide">
        {types.map((type) => (
          <button 
            key={type}
            onClick={() => setActiveType(type)}
            className={`px-4 py-1.5 rounded-lg text-xs font-semibold whitespace-nowrap transition-colors ${
              activeType === type 
                ? 'bg-[#e4fcf1] text-[#1a5b48] border border-transparent' 
                : 'bg-transparent text-foreground/80 border border-border hover:bg-muted'
            }`}
          >
            {type}
          </button>
        ))}
      </div>

      {/* Category Filters */}
      <div className="flex gap-2 mb-4 overflow-x-auto pb-1 scrollbar-hide">
        {categories.map((cat) => (
          <button 
            key={cat.name}
            onClick={() => setActiveCategory(cat.name)}
            className={`px-3 py-1 rounded-full text-[10px] font-medium whitespace-nowrap flex items-center gap-1 transition-colors ${
              activeCategory === cat.name 
                ? 'bg-[#c5f0da] text-[#1a5b48] border border-transparent font-bold' 
                : 'bg-transparent text-foreground/80 border border-transparent hover:bg-muted'
            }`}
          >
            {cat.color && <span className={`w-2 h-2 rounded-full ${cat.color}`}></span>}
            {cat.name}
          </button>
        ))}
      </div>

      {/* Summary Row */}
      <div className="flex justify-between items-center mb-10 px-1">
        <span className="text-[10px] text-muted-foreground font-medium">0 registro(s)</span>
        <span className="text-[10px] text-[#1db576] font-bold">Líquido: R$ 0,00</span>
      </div>

      {/* Empty State */}
      <motion.div 
        initial={{ opacity: 0, y: 10 }}
        animate={{ opacity: 1, y: 0 }}
        className="flex flex-col items-center justify-center text-center flex-1"
      >
        <span className="material-symbols-outlined text-[48px] text-muted-foreground/40 mb-4">filter_list</span>
        <h3 className="font-bold text-sm text-foreground mb-1">Nenhuma transação encontrada</h3>
        <p className="text-xs text-muted-foreground">
          Tente alterar os filtros ou o mês selecionado
        </p>
      </motion.div>
      
    </main>
  );
}

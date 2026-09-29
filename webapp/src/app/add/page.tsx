'use client';

import * as motion from "framer-motion/client";
import Link from 'next/link';
import { useRouter, useSearchParams } from 'next/navigation';
import { addTransactionAction } from '../actions/transactionActions';
import { useState, useEffect, Suspense } from 'react';

function AddTransactionForm() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const [loading, setLoading] = useState(false);
  const [type, setType] = useState<'EXPENSE' | 'INCOME'>('EXPENSE');

  useEffect(() => {
    const typeParam = searchParams.get('type');
    if (typeParam === 'INCOME' || typeParam === 'EXPENSE') {
      setType(typeParam);
    }
  }, [searchParams]);

  async function handleSubmit(formData: FormData) {
    setLoading(true);
    formData.append('type', type);
    // Para fins de dev sem auth implementado completo no browser:
    // Remover ou alterar isso quando o AuthState estiver 100% no cliente
    const res = await addTransactionAction(formData);
    
    setLoading(false);
    if (res?.error) {
      alert("Erro: " + res.error);
    } else {
      router.push('/');
    }
  }

  return (
    <main className="flex-1 flex flex-col p-6 max-w-md mx-auto w-full relative">
      <motion.div 
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        className="flex items-center gap-4 mb-8 pt-4"
      >
        <Link href="/" className="w-10 h-10 rounded-full bg-muted flex items-center justify-center text-foreground hover:bg-border transition-colors">
          <span className="material-symbols-outlined">arrow_back</span>
        </Link>
        <h1 className="text-xl font-bold tracking-tight text-foreground">
          Nova Transação
        </h1>
      </motion.div>

      <motion.form 
        action={handleSubmit}
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.1 }}
        className="flex flex-col gap-6"
      >
        {/* Toggle Tipo */}
        <div className="bg-muted p-1 rounded-2xl flex relative">
          <div className="flex-1 z-10 relative">
            <button 
              type="button"
              onClick={() => setType('EXPENSE')}
              className={`w-full py-3 text-sm font-bold rounded-xl transition-colors ${type === 'EXPENSE' ? 'text-destructive-foreground' : 'text-muted-foreground'}`}
            >
              Despesa
            </button>
          </div>
          <div className="flex-1 z-10 relative">
            <button 
              type="button"
              onClick={() => setType('INCOME')}
              className={`w-full py-3 text-sm font-bold rounded-xl transition-colors ${type === 'INCOME' ? 'text-success-foreground' : 'text-muted-foreground'}`}
            >
              Receita
            </button>
          </div>
          {/* Animated Background Indicator */}
          <div 
            className={`absolute top-1 bottom-1 w-[calc(50%-4px)] rounded-xl transition-all duration-300 ease-in-out ${type === 'EXPENSE' ? 'left-1 bg-destructive' : 'left-[calc(50%+2px)] bg-success'}`}
          />
        </div>

        <div className="flex flex-col gap-2">
          <label className="text-sm font-semibold text-foreground/80">Valor</label>
          <div className="relative">
            <span className="absolute left-4 top-1/2 -translate-y-1/2 text-muted-foreground font-bold">R$</span>
            <input 
              type="number" 
              name="amount"
              step="0.01"
              required
              placeholder="0,00"
              className="w-full bg-card border border-border rounded-2xl pl-12 pr-4 py-4 text-2xl font-bold text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50 transition-shadow"
            />
          </div>
        </div>

        <div className="flex flex-col gap-2">
          <label className="text-sm font-semibold text-foreground/80">Descrição</label>
          <input 
            type="text" 
            name="description"
            required
            placeholder="Ex: Almoço"
            className="w-full bg-card border border-border rounded-2xl px-4 py-4 font-medium text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50 transition-shadow"
          />
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div className="flex flex-col gap-2">
            <label className="text-sm font-semibold text-foreground/80">Data</label>
            <input 
              type="date" 
              name="date"
              required
              defaultValue={new Date().toISOString().split('T')[0]}
              className="w-full bg-card border border-border rounded-2xl px-4 py-4 font-medium text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50 transition-shadow"
            />
          </div>

          <div className="flex flex-col gap-2">
            <label className="text-sm font-semibold text-foreground/80">Categoria</label>
            <select 
              name="categoryId"
              required
              className="w-full bg-card border border-border rounded-2xl px-4 py-4 font-medium text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50 transition-shadow appearance-none"
            >
              <option value="alimentacao">Alimentação</option>
              <option value="transporte">Transporte</option>
              <option value="moradia">Moradia</option>
              <option value="salario">Salário</option>
              <option value="lazer">Lazer</option>
              <option value="outros">Outros</option>
            </select>
          </div>
        </div>

        {/* Opções Avançadas */}
        <div className="flex flex-col gap-4 p-4 rounded-2xl bg-muted/50 border border-border/50">
          <label className="flex items-center gap-3 cursor-pointer">
            <input 
              type="checkbox" 
              name="isRecurring"
              value="true"
              className="w-5 h-5 rounded border-border text-primary focus:ring-primary"
            />
            <span className="font-medium text-sm text-foreground/90">É uma transação recorrente (mensal)?</span>
          </label>
          
          <div className="flex items-center gap-3">
            <label className="text-sm font-semibold text-foreground/80 whitespace-nowrap">Parcelado em:</label>
            <input 
              type="number" 
              name="installmentsTotal"
              defaultValue="1"
              min="1"
              max="72"
              className="w-20 bg-card border border-border rounded-xl px-3 py-2 text-center font-bold text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50 transition-shadow"
            />
            <span className="text-sm text-muted-foreground">x vezes</span>
          </div>
        </div>

        <button 
          type="submit" 
          disabled={loading}
          className="mt-4 w-full bg-primary text-primary-foreground py-4 rounded-2xl font-bold text-lg hover:scale-[1.02] active:scale-[0.98] transition-transform disabled:opacity-50 disabled:pointer-events-none shadow-[0_8px_30px_rgb(0,105,72,0.2)]"
        >
          {loading ? 'Adicionando...' : 'Adicionar Transação'}
        </button>
      </motion.form>
    </main>
  );
}

export default function AddTransaction() {
  return (
    <Suspense fallback={<div className="p-6">Carregando...</div>}>
      <AddTransactionForm />
    </Suspense>
  );
}

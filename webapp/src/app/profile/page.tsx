'use client';

import * as motion from "framer-motion/client";
import { logoutAction } from '../actions/authActions';
import { useRouter } from 'next/navigation';
import { useState } from 'react';

export default function ProfilePage() {
  const router = useRouter();
  const [loading, setLoading] = useState(false);

  async function handleLogout() {
    setLoading(true);
    const res = await logoutAction();
    setLoading(false);
    
    if (res?.error) {
      alert("Erro ao sair: " + res.error);
    } else {
      router.push('/login');
    }
  }

  return (
    <main className="flex-1 flex flex-col p-4 max-w-md mx-auto w-full relative bg-[#f8f9ff] min-h-screen pb-24">
      
      {/* Header */}
      <div className="flex items-center justify-between mb-8 mt-2 px-2">
        <h1 className="text-xl font-bold text-foreground">
          Meu Perfil
        </h1>
      </div>

      {/* Profile Card */}
      <motion.div 
        initial={{ opacity: 0, y: 10 }}
        animate={{ opacity: 1, y: 0 }}
        className="bg-card rounded-3xl p-6 shadow-sm border border-border/50 mb-6 flex flex-col items-center gap-4 text-center"
      >
        <div className="w-20 h-20 rounded-full bg-[#1a5b48] flex items-center justify-center text-white text-3xl font-bold shadow-md">
          U
        </div>
        <div>
          <h2 className="font-bold text-lg text-foreground">Usuário</h2>
          <p className="text-sm text-muted-foreground">usuario@email.com</p>
        </div>
      </motion.div>

      {/* Subscription Status */}
      <motion.div 
        initial={{ opacity: 0, y: 10 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.05 }}
        className="bg-card rounded-3xl p-5 border border-primary/20 shadow-sm mb-6 flex items-center justify-between"
      >
        <div className="flex flex-col gap-1">
          <div className="flex items-center gap-2">
            <span className="material-symbols-outlined text-primary text-lg">verified</span>
            <span className="font-bold text-sm text-foreground">Plano Ativo</span>
          </div>
          <span className="text-xs text-muted-foreground font-medium">Assinatura Anual (R$ 29,00)</span>
        </div>
        <div className="bg-[#e4fcf1] text-[#1db576] px-3 py-1.5 rounded-lg text-[10px] font-bold">
          Ativo
        </div>
      </motion.div>

      {/* Menu Options */}
      <motion.div 
        initial={{ opacity: 0, y: 10 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.1 }}
        className="flex flex-col gap-3 mb-8"
      >
        <button className="bg-card w-full p-4 rounded-2xl flex items-center justify-between border border-border/50 shadow-sm hover:bg-muted transition-colors">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-full bg-blue-100 text-blue-600 flex items-center justify-center">
              <span className="material-symbols-outlined text-[20px]">person</span>
            </div>
            <span className="font-semibold text-sm text-foreground">Meus Dados</span>
          </div>
          <span className="material-symbols-outlined text-muted-foreground text-[20px]">chevron_right</span>
        </button>

        <button className="bg-card w-full p-4 rounded-2xl flex items-center justify-between border border-border/50 shadow-sm hover:bg-muted transition-colors">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-full bg-purple-100 text-purple-600 flex items-center justify-center">
              <span className="material-symbols-outlined text-[20px]">settings</span>
            </div>
            <span className="font-semibold text-sm text-foreground">Configurações</span>
          </div>
          <span className="material-symbols-outlined text-muted-foreground text-[20px]">chevron_right</span>
        </button>
      </motion.div>

      {/* Logout Button */}
      <motion.div 
        initial={{ opacity: 0, y: 10 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.2 }}
        className="mt-auto px-2"
      >
        <button 
          onClick={handleLogout}
          disabled={loading}
          className="w-full flex items-center justify-center gap-2 bg-destructive/10 text-destructive hover:bg-destructive/20 font-bold py-4 rounded-2xl transition-colors disabled:opacity-50"
        >
          <span className="material-symbols-outlined text-[20px]">logout</span>
          {loading ? 'Saindo...' : 'Sair da conta'}
        </button>
      </motion.div>

    </main>
  );
}

'use client';

import * as motion from "framer-motion/client";
import { loginAction } from '../actions/authActions';
import { useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { toast } from 'react-hot-toast';

export default function LoginPage() {
  const router = useRouter();
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);

  async function handleSubmit(formData: FormData) {
    setLoading(true);
    const res = await loginAction(formData);
    setLoading(false);

    if (res?.error) {
      toast.error(res.error);
    } else {
      toast.success("Login realizado com sucesso!");
      router.push('/');
    }
  }

  return (
    <main className="flex-1 flex flex-col items-center justify-center p-6 w-full h-screen bg-background relative overflow-hidden">
      {/* Background Decorator */}
      <div className="absolute top-0 left-0 w-full h-1/3 bg-primary/10 -skew-y-6 transform origin-top-left -z-10" />

      <motion.div 
        initial={{ opacity: 0, y: 30 }}
        animate={{ opacity: 1, y: 0 }}
        className="w-full max-w-sm bg-card p-8 rounded-[2rem] shadow-[0_8px_30px_rgb(0,0,0,0.04)] border border-border"
      >
        <div className="flex flex-col items-center mb-8 gap-3">
          <div className="w-14 h-14 bg-primary rounded-2xl flex items-center justify-center text-primary-foreground font-bold text-3xl shadow-lg">
            $
          </div>
          <h1 className="text-2xl font-extrabold tracking-tight text-foreground">Meu DinDin</h1>
          <p className="text-sm font-medium text-muted-foreground">Acesse sua conta para organizar</p>
        </div>

        <form action={handleSubmit} className="flex flex-col gap-5">
          <div className="flex flex-col gap-2">
            <label className="text-sm font-semibold text-foreground/80">E-mail</label>
            <input 
              type="email" 
              name="email"
              required
              placeholder="seu@email.com"
              className="w-full bg-muted/50 border border-border rounded-xl px-4 py-3 font-medium text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50 transition-shadow"
            />
          </div>

          <div className="flex flex-col gap-2">
            <div className="flex justify-between items-center">
              <label className="text-sm font-semibold text-foreground/80">Senha</label>
              <Link href="/forgot-password" className="text-xs font-bold text-primary hover:underline">
                Esqueceu a senha?
              </Link>
            </div>
            <div className="relative">
              <input 
                type={showPassword ? "text" : "password"}
                name="password"
                required
                placeholder="••••••••"
                className="w-full bg-muted/50 border border-border rounded-xl px-4 py-3 pr-12 font-medium text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50 transition-shadow"
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-3 top-1/2 -translate-y-1/2 p-1 text-muted-foreground hover:text-foreground transition-colors flex items-center justify-center"
                title={showPassword ? "Ocultar senha" : "Mostrar senha"}
              >
                <span className="material-symbols-outlined text-xl">
                  {showPassword ? "visibility_off" : "visibility"}
                </span>
              </button>
            </div>
          </div>

          <button 
            type="submit" 
            disabled={loading}
            className="mt-2 w-full bg-primary text-primary-foreground py-4 rounded-xl font-bold hover:scale-[1.02] active:scale-[0.98] transition-transform disabled:opacity-50 disabled:pointer-events-none shadow-[0_8px_30px_rgb(0,105,72,0.2)]"
          >
            {loading ? 'Entrando...' : 'Entrar'}
          </button>
        </form>
      </motion.div>
    </main>
  );
}

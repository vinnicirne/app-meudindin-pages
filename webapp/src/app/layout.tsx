import type { Metadata, Viewport } from "next";
import { Plus_Jakarta_Sans, Geist } from "next/font/google";
import "./globals.css";
import { cn } from "@/lib/utils";
import { Sidebar } from "@/components/layout/Sidebar";
import { BottomNav } from "@/components/layout/BottomNav";

const geist = Geist({subsets:['latin'],variable:'--font-sans'});

const plusJakartaSans = Plus_Jakarta_Sans({
  variable: "--font-plus-jakarta-sans",
  subsets: ["latin"],
  weight: ["400", "500", "600", "700", "800"],
});

export const metadata: Metadata = {
  title: "Meu DinDin | Controle Financeiro Descomplicado",
  description: "Seu dinheiro, sob seu controle, sem complicação.",
  manifest: "/manifest.json",
};

export const viewport: Viewport = {
  themeColor: "#006948",
  width: "device-width",
  initialScale: 1,
  maximumScale: 1,
  userScalable: false,
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html
      lang="pt-BR"
      className={cn("antialiased", "h-full", plusJakartaSans.variable, "font-sans", geist.variable)}
      suppressHydrationWarning
    >
      <head>
        <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" rel="stylesheet" />
      </head>
      <body className="min-h-full flex flex-col sm:flex-row bg-background text-foreground" suppressHydrationWarning>
        <Sidebar />
        <div className="flex-1 flex flex-col min-h-0 overflow-y-auto">
          {children}
        </div>
        <BottomNav />
      </body>
    </html>
  );
}

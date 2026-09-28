// Micro-animações sutis ao rolar a página
document.addEventListener("DOMContentLoaded", () => {
    const cards = document.querySelectorAll('.feature-card');
    
    const observer = new IntersectionObserver((entries) => {
        entries.forEach((entry, index) => {
            if (entry.isIntersecting) {
                setTimeout(() => {
                    entry.target.style.opacity = "1";
                    entry.target.style.transform = "translateY(0)";
                }, index * 100);
            }
        });
    }, { threshold: 0.1 });

    cards.forEach(card => {
        card.style.opacity = "0";
        card.style.transform = "translateY(20px)";
        card.style.transition = "all 0.6s cubic-bezier(0.16, 1, 0.3, 1)";
        observer.observe(card);
    });

    // Configuração dos botões de compra Mercado Pago
    const buyButtons = document.querySelectorAll('.buy-button');
    
    buyButtons.forEach(button => {
        button.addEventListener('click', async (e) => {
            e.preventDefault();
            
            // Alterar texto do botão para loading opcionalmente
            const originalHtml = button.innerHTML;
            button.innerHTML = '<span class="material-symbols-outlined text-[18px] animate-spin">refresh</span> Processando...';
            button.style.pointerEvents = 'none';

            try {
                // Chama nosso backend Serverless (Vercel)
                const response = await fetch('/api/create_preference', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    }
                });
                
                const data = await response.json();
                
                if (data.init_point) {
                    // Redireciona para o Checkout Pro do Mercado Pago
                    window.location.href = data.init_point;
                } else {
                    alert('Erro ao gerar pagamento. Tente novamente.');
                    button.innerHTML = originalHtml;
                    button.style.pointerEvents = 'auto';
                }
            } catch (error) {
                console.error('Erro:', error);
                alert('Erro ao conectar com o servidor. Tente novamente.');
                button.innerHTML = originalHtml;
                button.style.pointerEvents = 'auto';
            }
        });
    });
});

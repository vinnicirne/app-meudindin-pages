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
        button.addEventListener('click', (e) => {
            e.preventDefault();
            // Redireciona para a página de cadastro em vez de ir direto pro pagamento
            window.location.href = 'cadastro.html';
        });
    });
});

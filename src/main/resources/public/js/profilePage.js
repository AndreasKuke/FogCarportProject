document.addEventListener('DOMContentLoaded', () => {
    const payButton = document.querySelector('.pay-button');
    if (payButton) {
        payButton.addEventListener('click', event => {
            event.preventDefault();
            alert('Betaling bekræftet!');
        });
    }
});

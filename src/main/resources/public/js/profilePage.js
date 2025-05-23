document.addEventListener('DOMContentLoaded', () => {
    const payButton = document.querySelectorAll('.pay-button:not(:disabled)');
    payButton.forEach(button => {
        button.addEventListener('click', event => {
            event.preventDefault();
            alert('Betaling bekræftet! Du vil få sendt en mail med stykliste hurtigst muligt.');

            // Submit button complete
            button.closest('form').submit();
        });
    })
});

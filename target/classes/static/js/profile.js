document.addEventListener('DOMContentLoaded', function() {
    const inputs = document.querySelectorAll('input');
    const button = document.querySelector('.CartBtnEdit');
    
    const initialValues = {};
    inputs.forEach(input => {
        initialValues[input.placeholder] = input.value;
        input.addEventListener('input', () => {
            let isChanged = false;
            inputs.forEach(input => {
                if (input.value !== initialValues[input.placeholder]) {
                    isChanged = true;
                }
            });
            button.style.display = isChanged ? 'flex' : 'none';
        });
    });
});
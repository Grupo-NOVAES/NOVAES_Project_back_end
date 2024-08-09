document.addEventListener('DOMContentLoaded', function() {
    const userItems = document.querySelectorAll('.userItem');
    userItems.forEach(item => {
        item.addEventListener('dblclick', function() {
            const userId = item.getAttribute('data-id');
            window.location.href=`/user/profile/${userId}`;
        });
    });
});

document.getElementById('searchInput').addEventListener('input', function() {
    const filter = this.value.toLowerCase();
    const rows = document.querySelectorAll('#userTableBody tr');

    rows.forEach(row => {
        const nameCell = row.querySelector('td:nth-child(2)');
        if (nameCell) {
            const nameText = nameCell.textContent.toLowerCase();
            if (nameText.includes(filter)) {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        }
    });
});
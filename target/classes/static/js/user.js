document.addEventListener('DOMContentLoaded', function() {
    const userItems = document.querySelectorAll('.userItem');
    userItems.forEach(item => {
        item.addEventListener('dblclick', function() {
            const userId = item.getAttribute('data-id');
            window.location.href=`/user/profile/${userId}`;
        });
    });
});
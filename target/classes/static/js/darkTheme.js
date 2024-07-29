document.addEventListener('DOMContentLoaded', function () {
    const toggleThemeButton = document.getElementById("theme-checkbox");
    const body = document.body;
    const mainElement = document.querySelector('main');
    const headerElement = document.querySelector('header');
    const navElement = document.querySelector('nav');
    const navLinks = navElement.querySelectorAll('a'); 
    const tableElements = document.querySelectorAll('.table, table'); 
    const btnProfileElement = document.querySelector('.btnProfile'); 
    const CartBtnElement = document.querySelector('.CartBtn');
    const activeElement = document.querySelector('.nav-item');

    const isDarkTheme = body.classList.contains("dark-theme");

    navLinks.forEach(link => {
        if (isDarkTheme) {
            link.classList.add("dark-theme-a");
        } else {
            link.style.color = "#1e3050";
        }
    });

    tableElements.forEach(tableElement => {
        if (isDarkTheme) {
            tableElement.classList.add("dark-theme-table");
        }
    });

    if (isDarkTheme) {
        btnProfileElement.classList.add("dark-theme");
    } else {
        btnProfileElement.classList.remove("dark-theme");
    }

    if (isDarkTheme) {
        CartBtnElement.classList.add("dark-theme");
    }else {
        CartBtnElement.classList.remove("dark-theme");
    }

    if (isDarkTheme) {
        activeElement.classList.add("dark-theme");
    }else {
        activeElement.classList.remove("dark-theme");
    }

    toggleThemeButton.addEventListener("click", function () {
        const isDarkTheme = body.classList.toggle("dark-theme");
        mainElement.classList.toggle("dark-theme");
        headerElement.classList.toggle("dark-theme-header");
        navElement.classList.toggle("dark-theme-nav");

        navLinks.forEach(link => {
            if (isDarkTheme) {
                link.classList.add("dark-theme-a");
                link.style.color = ""; 
            } else {
                link.classList.remove("dark-theme-a");
                link.style.color = "#1e3050";
            }
        });

        tableElements.forEach(tableElement => {
            if (isDarkTheme) {
                tableElement.classList.add("dark-theme-table");
            } else {
                tableElement.classList.remove("dark-theme-table");
            }
        });

        if (isDarkTheme) {
            btnProfileElement.classList.add("dark-theme");
        } else {
            btnProfileElement.classList.remove("dark-theme");
        }

        if (isDarkTheme) {
            CartBtnElement.classList.add("dark-theme");
        }else{
            CartBtnElement.classList.remove("dark-theme");
        }

        if (isDarkTheme) {
            activeElement.classList.add("dark-theme");
        }else {
            activeElement.classList.remove("dark-theme");
        }
        
    });
});
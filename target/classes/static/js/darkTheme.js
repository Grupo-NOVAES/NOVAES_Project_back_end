const toggleThemeButton = document.getElementById("theme-checkbox");
const body = document.body;
const mainElement = document.querySelector('main');
const headerElement = document.querySelector('header');
const navElement = document.querySelector('nav');
const navLinks = navElement.querySelectorAll('a');
const tableElements = document.querySelectorAll('.table, table');
const btnProfileElement = document.querySelector('.btnProfile');
const cardBtnElement = document.querySelector('.CartBtn');
const cardBtnEditElement = document.querySelector('.CartBtnEdit');
const activeElement = document.querySelector('.nav-item');
const btnBackDirectory = document.querySelector('.buttonBackDirectory');
const btnAlterInfoProfile = document.getElementById("saveButton");

function alterTheme() {
    const isDarkTheme = body.classList.contains("dark-theme");

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

    if (cardBtnElement != null) {
        if (isDarkTheme) {
            cardBtnElement.classList.add("dark-theme");
        } else {
            cardBtnElement.classList.remove("dark-theme");
        }
    }

    if (cardBtnEditElement != null) {
        if (isDarkTheme) {
            cardBtnEditElement.classList.add("dark-theme");
        } else {
            cardBtnEditElement.classList.remove("dark-theme");
        }
    }

    if (btnAlterInfoProfile != null) {
        if (isDarkTheme) {
            btnAlterInfoProfile.classList.add("dark-theme");
        } else {
            btnAlterInfoProfile.classList.remove("dark-theme");
        }
    }

    if (activeElement != null) {
        if (isDarkTheme) {
            activeElement.classList.add("dark-theme");
        } else {
            activeElement.classList.remove("dark-theme");
        }
    }
}

toggleThemeButton.addEventListener("click", function () {
    const isDarkTheme = body.classList.toggle("dark-theme");
    mainElement.classList.toggle("dark-theme");
    headerElement.classList.toggle("dark-theme-header");
    navElement.classList.toggle("dark-theme-nav");

    alterTheme(); 

    setCookie("theme", isDarkTheme ? "dark" : "light", 7);
});

function setCookie(name, value, days) {
    let expires = "";
    if (days) {
        const date = new Date();
        date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
        expires = "; expires=" + date.toUTCString();
    }
    document.cookie = name + "=" + (value || "") + expires + "; path=/";
}

function getCookie(name) {
    const nameEQ = name + "=";
    const ca = document.cookie.split(';');
    for (let i = 0; i < ca.length; i++) {
        let c = ca[i];
        while (c.charAt(0) === ' ') c = c.substring(1, c.length);
        if (c.indexOf(nameEQ) === 0) return c.substring(nameEQ.length, c.length);
    }
    return null;
}

function loadTheme() {
    const theme = getCookie("theme");
    if (theme === "dark") {
        document.getElementById("theme-checkbox").checked = true;
        body.classList.add("dark-theme");
        mainElement.classList.add("dark-theme");
        headerElement.classList.add("dark-theme-header");
        navElement.classList.add("dark-theme-nav");
    }
    alterTheme(); 
}

document.addEventListener('DOMContentLoaded', loadTheme);

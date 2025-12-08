document.getElementById("nav").innerHTML = `
<style>
    @import url('https://fonts.googleapis.com/css2?family=Tilt+Warp&display=swap');

    .navBar {
        list-style-type: none;
        display: flex;
        align-items: center;
        padding: 10px;
    }

    .navItem {
        margin-left: 3rem;
        font-family: "Verdana", sans-serif;
        font-size: 1.2rem;
    }

    .navItem a, .logo a, .profile a {
        text-decoration: none;
        color: black;
    }

    .logo {
        width: 10rem;
        margin-right: 2rem;
        font-family: "Tilt Warp", sans-serif;
        font-size: 1.8rem;
        font-weight: bold;
    }

    .profile {
        width: 10rem;
        margin-left: 5rem;
        font-size: 1.2rem;
    }

    .profile a {
        padding: 3px;
        padding-left: 6px;
        padding-right: 6px;
        border-style: solid;
        border-radius: 8px;
    }

    .text-button {
        background: none;
        border: none;
        padding: 0;
        font: inherit;
        cursor: pointer;
        text-decoration: none;
        font-weight: bold;
        font-size: 1.5rem;
    }

    .mode {
        font-weight: bold;
    }
</style>

<ul class="navBar">
    <li class="logo"><a href="/">Flufan</a></li>
    <li class="navItem"><a href="/">Leaderboard</a></li>
    <li class="navItem"><a href="/">Settings</a></li>
    <li class="navItem"><a href="/about">FAQ</a></li>
    <li class="profile"><a href="/templates/login.html">Sign In</a></li>
</ul>
`;
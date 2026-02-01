function showSettings(id){

    // Fjerner active-klassen
    document.querySelectorAll(".settings").forEach(s => {
        s.classList.remove("active");
    });

    // Legg til active-klassen der ID-en matcher :)
    const btn = document.getElementById(id);
    
    btn.classList.add("active");

}

function getUserInfo(){
    
}
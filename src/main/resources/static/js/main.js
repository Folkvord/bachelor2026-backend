function format(){

    const input = document.getElementById("jsonInput").value;

    try{
        const parsed = JSON.parse(input);
        document.getElementById("jsonInput").value = JSON.stringify(parsed, null, 2);
        document.getElementById("result").innerText = "Gydlig JSON";
    }
    catch(e){
        document.getElementById("result").innerText = "Ikke gyldig JSON:\n" + e.message;
    }

}


function validate(){

    const input = document.getElementById("jsonInput").value;

    try{
        JSON.parse(input);
        document.getElementById("result").innerText = "Gydlig JSON";
    }
    catch(e){
        document.getElementById("result").innerText = "Ikke gyldig JSON:\n" + e.message;

    }

}


async function loadTask(){

    const name = document.getElementById("searchName").value;

    if(!name){
        document.getElementById("searchStatus").value = "Skriv et navn";
        return;
    }

    document.getElementById("searchStatus").innerText = "Laster...";

    try{
        const response = await fetch("/api/task/edit/" + encodeURIComponent(name));
        const data = await response.json();

        if(data.has("error")){
            document.getElementById("searchStatus").innerText = "Fant ikke oppgaven";
            return;
        }

        document.getElementById("nameField").value = data.name || "";
        document.getElementById("descriptionField").value = data.description || "";

        const jsonData = typeof data.task === "string" ? JSON.parse(data.task) : data.task;

        document.getElementById("jsonInput").value = JSON.stringify(jsonData, null, 2);
        document.getElementById("searchStatus").innerText = "";
    }
    catch(e){
        document.getElementById("searchStatus").innerText = "Kunne ikke laste inn oppgave";
    }

}


async function save(){

    const name = document.getElementById("nameField");
    const desc = document.getElementById("descriptionField");
    const task = document.getElementById("jsonInput").value;

    let parsedJson;

    try{
        parsedJson = JSON.parse(task);
    }catch(e){
        alert("JSON er ugyldig");
        return;
    }

    const payload = {
        name: name,
        desc: desc,
        task: parsedJson
    }

    try{
        const response = await fetch("/api/task/edit/" + encodeURIComponent(name), {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });
    
        if(!response.ok){
            throw new Error("oida");
        }
    
        alert("Oppgaven ble lagret");
    }
    catch(e){
        alert("Kunne ikke lagre");
    }

}



// Gi muligheten for å bruke tab i oppgaveeditoren
const textarea = document.getElementById("jsonInput");

textarea.addEventListener("keydown", (e) => {
    if (e.keyCode === 9) {
        e.preventDefault();
        textarea.setRangeText(
            "\t",
            textarea.selectionStart,
            textarea.selectionStart,
            "end"
        );
    }
});
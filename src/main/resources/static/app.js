let jetonCourant = "";
let rolesCourants = [];

const champJeton = document.getElementById("champ-jeton");
const champIdentifiant = document.getElementById("champ-identifiant");
const champMotDePasse = document.getElementById("champ-mot-de-passe");
const etiquetteRoles = document.getElementById("etiquette-roles");
const corpsJournal = document.getElementById("corps-journal");
const reponseRoute = document.getElementById("reponse-route");
const reponseContenu = document.getElementById("reponse-contenu");
const codeHttp = document.getElementById("code-http");

const routes = {
    "public-etat": { methode: "GET", url: "/api/public/etat", jeton: false },
    "moi": { methode: "GET", url: "/api/auth/moi", jeton: true },
    "membre": { methode: "GET", url: "/api/membre/tableau-de-bord", jeton: true },
    "admin-stats": { methode: "GET", url: "/api/administration/statistiques", jeton: true },
    "admin-comptes": { methode: "GET", url: "/api/administration/comptes", jeton: true },
    "sans-jeton": { methode: "GET", url: "/api/membre/tableau-de-bord", jeton: false },
    "jeton-falsifie": {
        methode: "GET",
        url: "/api/membre/tableau-de-bord",
        jeton: false,
        force: "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhLmJlbmlkYXIiLCJleHAiOjE5MDAwMDAwMDB9.xx"
    }
};

document.querySelectorAll(".puce").forEach(puce => {
    puce.addEventListener("click", () => {
        champIdentifiant.value = puce.dataset.id;
        champMotDePasse.value = puce.dataset.mdp;
    });
});

document.getElementById("formulaire-connexion").addEventListener("submit", async evenement => {
    evenement.preventDefault();
    const charge = {
        identifiant: champIdentifiant.value.trim(),
        motDePasse: champMotDePasse.value
    };

    const reponse = await fetch("/api/auth/connexion", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(charge)
    });

    const corps = await reponse.json();
    journaliser("POST", "/api/auth/connexion", false, reponse.status);
    afficher("POST /api/auth/connexion", reponse.status, corps);

    if (reponse.ok) {
        jetonCourant = corps.jeton;
        rolesCourants = corps.roles || [];
        champJeton.value = jetonCourant;
        etiquetteRoles.textContent = rolesCourants.join(" + ") || "sans rôle";
        etiquetteRoles.className = "etiquette " + (rolesCourants.includes("ROLE_ADMIN") ? "admin" : "membre");
    } else {
        jetonCourant = "";
        rolesCourants = [];
        champJeton.value = "";
        etiquetteRoles.textContent = "aucun jeton";
        etiquetteRoles.className = "etiquette";
    }
});

document.querySelectorAll("[data-appel]").forEach(bouton => {
    bouton.addEventListener("click", async () => {
        const route = routes[bouton.dataset.appel];
        const entetes = {};
        let jetonEnvoye = false;

        if (route.force) {
            entetes["Authorization"] = "Bearer " + route.force;
            jetonEnvoye = true;
        } else if (route.jeton && jetonCourant) {
            entetes["Authorization"] = "Bearer " + jetonCourant;
            jetonEnvoye = true;
        }

        const reponse = await fetch(route.url, { method: route.methode, headers: entetes });
        let corps;
        const texte = await reponse.text();
        try {
            corps = JSON.parse(texte);
        } catch (e) {
            corps = texte;
        }

        journaliser(route.methode, route.url, jetonEnvoye, reponse.status);
        afficher(route.methode + " " + route.url, reponse.status, corps);
    });
});

function afficher(route, statut, corps) {
    reponseRoute.textContent = route;
    codeHttp.textContent = String(statut);
    codeHttp.className = "code-http " + (statut < 400 ? "ok" : "ko");
    reponseContenu.textContent = typeof corps === "string" ? corps : JSON.stringify(corps, null, 2);
}

function journaliser(methode, url, avecJeton, statut) {
    const vide = corpsJournal.querySelector(".journal__vide");
    if (vide) {
        vide.remove();
    }

    let mentionJeton = "aucun";
    if (avecJeton) {
        const fin = jetonCourant ? jetonCourant.slice(-8) : "falsifié";
        mentionJeton = "Bearer …" + fin;
    }

    const classe = statut < 300 ? "ok" : (statut === 403 ? "warn" : "ko");
    const ligne = document.createElement("tr");
    ligne.innerHTML = `
        <td>${new Date().toLocaleTimeString("fr-FR")}</td>
        <td>${methode}</td>
        <td>${url}</td>
        <td>${mentionJeton}</td>
        <td><span class="pastille ${classe}">${statut}</span></td>
    `;
    corpsJournal.prepend(ligne);
}

async function verifierEtat() {
    const bloc = document.getElementById("etat-service");
    try {
        const reponse = await fetch("/api/public/etat");
        const corps = await reponse.json();
        bloc.textContent = "service " + corps.statut + " · " + corps.dossiersEnregistres + " dossiers";
        bloc.className = "bandeau__etat ok";
    } catch (e) {
        bloc.textContent = "service indisponible";
        bloc.className = "bandeau__etat ko";
    }
}

verifierEtat();

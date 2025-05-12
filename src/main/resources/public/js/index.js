document.addEventListener("DOMContentLoaded", () => {
    const button = document.getElementById("get-guiding-price");

    button.addEventListener("click", async () => {
        const width = document.getElementById("carport-width-selection").value;
        const length = document.getElementById("carport-length-selection").value;

        if (!width || !length) {
            alert("Vælg både længde og bredde først.");
            return;
        }

        try {
            const response = await fetch(`/calculate-price?carport-width-selection=${width}&carport-length-selection=${length}`);
            if (!response.ok) {
                throw new Error("Kunne ikke hente prisen.");
            }
            const price = await response.json();
            alert(`Vejledende pris: ${price} kr.`);
        } catch (error) {
            alert("Fejl: " + error.message);
        }
    });
});

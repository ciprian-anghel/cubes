## Inainte de toate:
	- prin `textura`, intelegem un fisier cu extensia `.png`.
	- prin `icon`, intelegem un fisier cu prefixul `icon-` si extensia `.png`.
	- prin `parinte`, intelegem directorul parinte al unei texturi.

## 1. Strucura
	- Fiecare `textura` si fiecare `parinte` va deveni in final un buton in UI.
	//TODO: schimb logica in server ca citesc doar .png si sa ignor restul.
		
## 2. Numele `parintelui` identic cu numele mesh-ului modelului 3D. 
	- `Parintele` trebuie sa coincida cu numele unui mesh al modelului 3D. 
	- De exemplu:
	- /cubes/head/asset-1.png
	- "head" este numele `parintelui` `texturii` "asset-1.png"
	- `Textura` "asset-1.png" va fi aplicata asupra mesh-ului cu numele "head" al modelului 3D
	
## 2. Icons
	- ".png" urile cu prefixul "icon-" vor fi tratate ca icon.
	- fiecare `icon` se va afla la acelasi nivel ierarhic cu `parintele` sau `textura` asociata acestuia.
	- numele `iconului` va fi compus din prefixul "icon-" si numele `texturii` sau `parintelui`, incluzand extensia ".png"
	

for await (const f of Deno.readDir('textures/pbentity')) {
	Deno.writeTextFileSync("models/pbentity/" + f.name.substring(0, f.name.length - 4) + ".json", JSON.stringify({
		parent: "illagerexp:pbentity/generic_illager_head",
		"textures": {
			"0": "illagerexp:pbentity/" + f.name.substring(0, f.name.length - 4)
		}
	}))
}
# Anatomity 3D model setup

The 3D screen is already wired to SceneView 4.32.0.

## Add models

Copy `.glb` files to:

`app/src/main/assets/models/`

Recommended four-file names:

- `skeleton.glb`
- `muscles.glb`
- `brain.glb`
- `organs.glb`

System-specific files such as `respiratory.glb` override the shared file automatically.

## Controls

- One-finger drag: orbit/rotate camera around the anatomy model.
- Pinch: zoom.
- Two-finger drag: pan.
- Tap a named renderable mesh: select it and show its Anatomity information card.

## How selection works

The viewer disables the single whole-model touch target and creates a collision box for each renderable child in the GLB. The tapped child's glTF node name is matched against the existing Anatomity part definitions.

Names are normalized, so exports like `Left_Femur`, `heart.001`, and `Mesh_Right_Lung` can often match automatically.

For names that do not match, edit:

`app/src/main/assets/anatomy/model_part_aliases.json`

Example:

```json
{
  "skeletal": {
    "Object_1234": "Femur"
  }
}
```

The right-hand value must match an anatomy part name already defined in `MainActivity.kt`.

## Important model requirement

A GLB that contains the entire anatomy as one merged renderable mesh cannot provide per-part selection. The organs/bones/muscles must remain separate renderable nodes/meshes with names.

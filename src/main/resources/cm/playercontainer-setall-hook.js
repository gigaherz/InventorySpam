function initializeCoreMod() {
	return {
		'InventorySpam PlayerContainer Transformer': {
			'target': {
				'type': 'CLASS',
				'name': "net.minecraft.inventory.container.PlayerContainer"
			},
			'transformer': function(classNode) {

                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
                var MethodNode = Java.type('org.objectweb.asm.tree.MethodNode');
                var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
                var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');

	            var setAllName = ASMAPI.mapMethod("func_190896_a");

                var method = new MethodNode(
                    /* access = */ Opcodes.ACC_PUBLIC,
                    /* name = */ setAllName,
                    /* descriptor = */ "(Ljava/util/List;)V",
                    /* signature = */ "(Ljava/util/List<Lnet/minecraft/item/ItemStack;>;)V",
                    /* exceptions = */ null
                );

                method.instructions.add(new LabelNode()); // super call
	            method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0 /*this*/));
	            method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1 /*items*/));
	            method.instructions.add(new MethodInsnNode(
	                Opcodes.INVOKESPECIAL, "net/minecraft/inventory/container/RecipeBookContainer", setAllName, "(Ljava/util/List;)V", false
	            ));

                method.instructions.add(new LabelNode()); // hook call
	            method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0 /*this*/));
	            method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1 /*items*/));
	            method.instructions.add(new MethodInsnNode(
	                Opcodes.INVOKESTATIC, "gigaherz/inventoryspam/PlayerContainerHooks", "afterSetAll", "(Lnet/minecraft/inventory/container/PlayerContainer;Ljava/util/List;)V", false
	            ));

                method.instructions.add(new LabelNode()); // return
                method.instructions.add(new InsnNode(Opcodes.RETURN));

				classNode.methods.add(method);

				return classNode;
			}
		}
	}
}

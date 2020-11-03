function initializeCoreMod() {
    var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
    var Opcodes = Java.type('org.objectweb.asm.Opcodes');
    var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
    var MethodNode = Java.type('org.objectweb.asm.tree.MethodNode');
    var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
    var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
    var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');

    function addAll(insList) {
        var i;
        for(i =1;i<arguments.length;i++)
        {
            insList.add(arguments[i]);
        }
    }

    function label() { return new LabelNode(); }

    function aload(n) { return new VarInsnNode(Opcodes.ALOAD, n); }
    function aload0() { return new VarInsnNode(Opcodes.ALOAD, 0); }
    function aload1() { return new VarInsnNode(Opcodes.ALOAD, 1); }

    function invokeSpecial(ownerClass, method, signature, isInterface) {
        return new MethodInsnNode(
            Opcodes.INVOKESPECIAL, ownerClass, method, signature, isInterface
        );
    }

    function invokeStatic(ownerClass, method, signature, isInterface) {
        return new MethodInsnNode(
            Opcodes.INVOKESTATIC, ownerClass, method, signature, isInterface
        );
    }

    function ret() { return new InsnNode(Opcodes.RETURN); }

    return {
        'InventorySpam PlayerContainer Transformer': {
            'target': {
                'type': 'CLASS',
                'name': "net.minecraft.inventory.container.PlayerContainer"
            },
            'transformer': function(classNode) {
                var name_setAll = ASMAPI.mapMethod("func_190896_a");

                var method = new MethodNode(
                    /* access = */ Opcodes.ACC_PUBLIC,
                    /* name = */ name_setAll,
                    /* descriptor = */ "(Ljava/util/List;)V",
                    /* signature = */ "(Ljava/util/List<Lnet/minecraft/item/ItemStack;>;)V",
                    /* exceptions = */ null
                );

                // super call
                addAll(method.instructions,
                    label(),
                    aload0(), /*this*/
                    aload1(), /*items*/
                    invokeSpecial(
                        "net/minecraft/inventory/container/RecipeBookContainer",
                        name_setAll, "(Ljava/util/List;)V", false
                    )
                );

                // hook call
                addAll(method.instructions,
                    label(),
                    aload0(), /*this*/
                    aload1(), /*items*/
                    invokeStatic(
                        "gigaherz/inventoryspam/PlayerContainerHooks",
                        "afterSetAll", "(Lnet/minecraft/inventory/container/PlayerContainer;Ljava/util/List;)V", false
                    )
                );

                // return
                addAll(method.instructions,
                    label(),
                     ret()
                );

                classNode.methods.add(method);

                return classNode;
            }
        }
    }
}

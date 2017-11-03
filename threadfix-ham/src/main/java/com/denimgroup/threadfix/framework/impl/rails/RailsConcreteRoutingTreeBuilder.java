package com.denimgroup.threadfix.framework.impl.rails;

import com.denimgroup.threadfix.framework.impl.rails.model.AbstractRailsRoutingEntry;
import com.denimgroup.threadfix.framework.impl.rails.model.RailsRouter;
import com.denimgroup.threadfix.framework.impl.rails.model.RailsRoutingEntry;
import com.denimgroup.threadfix.framework.impl.rails.model.defaultRoutingEntries.DrawEntry;

import javax.annotation.Nonnull;
import java.util.Collection;

public class RailsConcreteRoutingTreeBuilder implements RailsAbstractTreeVisitor {

    Collection<RailsRouter> routers;

    RailsRoutingEntry currentEntry;
    RailsRoutingEntry currentScope;
    RailsAbstractRoutingDescriptor lastDescriptor = null;

    public RailsConcreteRoutingTreeBuilder(@Nonnull Collection<RailsRouter> routers) {
        this.routers = routers;
    }

    public RailsConcreteRoutingTree buildFrom(RailsAbstractRoutingTree abstractTree) {
        RailsConcreteRoutingTree concreteTree = new RailsConcreteRoutingTree();

        RailsRoutingEntry rootConcreteEntry = new DrawEntry();
        RailsAbstractRoutingDescriptor rootAbstractEntry = abstractTree.getRootDescriptor();

        rootConcreteEntry.onBegin(rootAbstractEntry.getIdentifier());
        concreteTree.setRootEntry(rootConcreteEntry);

        currentScope = rootConcreteEntry;
        currentEntry = rootConcreteEntry;
        lastDescriptor = rootAbstractEntry;

        abstractTree.walkTree(this);

        if (currentEntry != null) {
            currentEntry.onEnd();
        }

        currentEntry = null;
        currentScope = null;
        lastDescriptor = null;

        return concreteTree;
    }

    private RailsRoutingEntry makeRouteEntry(String identifier) {
        RailsRoutingEntry result = null;
        for (RailsRouter router : routers) {
            result = router.identify(identifier);
            if (result != null) break;
        }
        return result;
    }

    @Override
    public void acceptDescriptor(RailsAbstractRoutingDescriptor descriptor) {
        if (descriptor == lastDescriptor) {
            return;
        }

        if (lastDescriptor != null) {
            boolean droppedInScope =
                    lastDescriptor.getParentDescriptor() != descriptor.getParentDescriptor()
                            && descriptor.getParentDescriptor() != lastDescriptor;

            if (droppedInScope) {
                currentScope = currentScope.getParent();
            }
        }

        if (currentEntry != null) {
            currentEntry.onEnd();
        }

        boolean raisedScope = lastDescriptor != null && (descriptor.getParentDescriptor() == lastDescriptor);
        if (raisedScope) {
            currentScope = currentEntry;
        }

        RailsRoutingEntry entry = makeRouteEntry(descriptor.getIdentifier());
        entry.onBegin(descriptor.getIdentifier());
        entry.setParent(currentScope);
        currentScope.addChildEntry(entry);
        currentEntry = entry;

        lastDescriptor = descriptor;
    }

    @Override
    public void acceptParameter(RailsAbstractParameter parameter) {
        currentEntry.onParameter(parameter.getName(), parameter.getValue(), parameter.getParameterType());
    }
}

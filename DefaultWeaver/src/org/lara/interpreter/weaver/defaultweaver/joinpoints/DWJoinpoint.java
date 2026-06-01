package org.lara.interpreter.weaver.defaultweaver.joinpoints;

import org.lara.interpreter.weaver.defaultweaver.abstracts.joinpoints.AJoinpoint;
import org.lara.interpreter.weaver.interf.enums.InsertPosition;
import org.lara.interpreter.weaver.defaultweaver.DWWeaver;
import java.lang.Object;

public class DWJoinpoint<Self extends DWJoinpoint<Self>> extends AJoinpoint<Self> {
    protected DWJoinpoint(Object node, DWWeaver weaver) {
        super(node, weaver);
    }

    @Override
    public DWWeaver getWeaverEngine() {
        return (DWWeaver) super.getWeaverEngine();
    }

    @Override
    public AJoinpoint<?>[] getChildrenImpl() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getChildrenImpl'");
    }

    @Override
    public AJoinpoint<?>[] getDescendantsImpl() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDescendantsImpl'");
    }

    @Override
    public AJoinpoint<?>[] getScopeNodesImpl() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getScopeNodesImpl'");
    }

    @Override
    public AJoinpoint<?> getParentImpl() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getParentImpl'");
    }

    @Override
    public AJoinpoint<?> getRootImpl() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRootImpl'");
    }

    @Override
    public String getCodeImpl() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCodeImpl'");
    }

    @Override
    public Integer getLineImpl() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getLineImpl'");
    }

    @Override
    public Integer getColumnImpl() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getColumnImpl'");
    }

    @Override
    public boolean getEqualsImpl(Self jp) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getEqualsImpl'");
    }

    @Override
    public boolean getCompareNodesImpl(AJoinpoint<?> aJoinPoint) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCompareNodesImpl'");
    }

    @Override
    public boolean getSameImpl(AJoinpoint<?> other) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSameImpl'");
    }

    @Override
    public AJoinpoint<?>[] insertImpl(InsertPosition position, String code) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'insertImpl'");
    }

    @Override
    public AJoinpoint<?>[] insertImpl(InsertPosition position, AJoinpoint<?> joinpoint) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'insertImpl'");
    }

}

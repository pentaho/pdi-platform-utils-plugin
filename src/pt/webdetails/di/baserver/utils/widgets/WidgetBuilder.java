package pt.webdetails.di.baserver.utils.widgets;

import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.ui.core.PropsUI;

public abstract class WidgetBuilder {
  // general properties
  protected PropsUI props;
  protected Composite parent;
  // placement
  private Control top = null;
  // listeners
  private ModifyListener modifyListener = null;

  public WidgetBuilder( Composite parent, PropsUI props ) {
    this.parent = parent;
    this.props = props;
  }

  public abstract Text build();

  public TestBoxBuilder setTop( Control top ) {
    this.top = top;
    return this;
  }

  public TestBoxBuilder setModifyListener( ModifyListener modifyListener ) {
    this.modifyListener = modifyListener;
    return this;
  }
}

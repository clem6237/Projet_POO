package metier;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author clementruffin
 */
@Entity
@Table(name = "TOUR")
@XmlRootElement
public class Tour implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private int id;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idTour")
    private Collection<Route> listRoutes;

    public Tour() {
    }

    public Tour(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Collection<Route> getListRoutes() {
        return listRoutes;
    }

    public void setListRoutes(Collection<Route> listRoutes) {
        this.listRoutes = listRoutes;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Tour other = (Tour) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Tour{" 
                + "id=" + id 
                + ", listRoutes=" + listRoutes 
                + "}";
    }
}

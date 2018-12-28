package by.koles.springrest.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import by.koles.springrest.entity.Customer;

@Repository
public class CustomerDAOImpl implements CustomerDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public void deleteCustomer(int id) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query query = currentSession.createQuery("DELETE from Customer WHERE id=:customerId");
		query.setParameter("customerId", id);
		query.executeUpdate();
	}

	@Override
	public List<Customer> getCustomers() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Customer> query = currentSession.createQuery("from Customer order by lastName", Customer.class);
		List<Customer> customers = query.getResultList();
		return customers;
	}

	@Override
	public void saveCustomer(Customer customer) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.saveOrUpdate(customer);
	}

	@Override
	public Customer getCustomer(int id) {
		Session currentSession = sessionFactory.getCurrentSession();
		return currentSession.get(Customer.class, id);
	}

	@Override
	public List<Customer> searchCustomers(String searchName) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query query = null;
		if (searchName != null && searchName.trim().length() > 0) {
			query = currentSession.createQuery(
					"FROM Customer WHERE lower(firstName) like :name OR lower(lastName) like :name", Customer.class);
			query.setParameter("name","%" + searchName.toLowerCase() + "%");
		} else {
			query = currentSession.createQuery("FROM Customer", Customer.class);
		}
		List<Customer> customers = query.getResultList();
		return customers;
	}

}

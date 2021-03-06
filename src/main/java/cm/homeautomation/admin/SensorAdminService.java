package cm.homeautomation.admin;

import java.util.List;

import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import cm.homeautomation.db.EntityManagerService;
import cm.homeautomation.entities.Room;
import cm.homeautomation.entities.Sensor;
import cm.homeautomation.services.base.GenericStatus;

@Path("admin/sensor")
public class SensorAdminService {

	@GET
	@Path("create/{roomId}/{name}/{type}")
	public GenericStatus createSensor(@PathParam("roomId") Long roomId, @PathParam("name") String name,
			@PathParam("type") String type) {
		GenericStatus genericStatus = new GenericStatus(true);
		EntityManager em = EntityManagerService.getNewManager();

		@SuppressWarnings("unchecked")
		List<Room> rooms = (List<Room>) em.createQuery("select r from Room r where r.id=:roomId")
				.setParameter("roomId", roomId).getResultList();

		for (Room room : rooms) {

			Sensor sensor = new Sensor();
			sensor.setSensorName(name);
			sensor.setSensorType(type);
			sensor.setRoom(room);
			room.getSensors().add(sensor);

			em.getTransaction().begin();
			em.persist(sensor);
			em.merge(room);
			em.getTransaction().commit();

			genericStatus.setObject(sensor);
		}
		em.close();
		
		return genericStatus;
	}

	@GET
	@Path("update/{sensorId}/{name}/{type}")
	public GenericStatus updateSensor(@PathParam("sensorId") Long sensorId, @PathParam("name") String name,
			@PathParam("type") String type) {

		EntityManager em = EntityManagerService.getNewManager();

		@SuppressWarnings("unchecked")
		List<Sensor> sensors = (List<Sensor>) em.createQuery("select s from Sensor s where s.id=:sensorId")
				.setParameter("sensorId", sensorId).getResultList();

		if (sensors != null) {
			em.getTransaction().begin();
			for (Sensor sensor : sensors) {
				sensor.setSensorName(name);
				sensor.setSensorType(type);
				em.persist(sensor);
			}
			
			em.getTransaction().commit();
		}
		
		em.close();
		return new GenericStatus(true);
	}

	@GET
	@Path("delete/{sensorId}")
	public GenericStatus deleteSensor(@PathParam("sensorId") Long sensorId) {

		EntityManager em = EntityManagerService.getNewManager();

		@SuppressWarnings("unchecked")
		List<Sensor> sensors = (List<Sensor>) em.createQuery("select s from Sensor s where s.id=:sensorId")
				.setParameter("sensorId", sensorId).getResultList();

		if (sensors != null) {
			em.getTransaction().begin();
			for (Sensor sensor : sensors) {
				Room room = sensor.getRoom();
				room.getSensors().remove(sensor);

				em.persist(room);
				em.remove(sensor);
			}
			em.getTransaction().commit();
		}
		
		em.close();

		return new GenericStatus(true);
	}
}

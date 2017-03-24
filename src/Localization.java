import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class Localization {

	private int NUMBER_OF_PARTICLES;

	private ArrayList<Particle> particles;
	private Location[][] map;
	private int maxX, maxY;
	private BufferedReader r;

	public Localization() {
		r = new BufferedReader(new InputStreamReader(System.in));
		createMap();
		createParticles();
	}

	public void localize() {
		try {
			while (!isConverged()) {
				int move = Integer.parseInt(r.readLine());

				if (move == 1) {
					updateParticles(Direction.FORWARD);
				} else if (move == 2) {
					updateParticles(Direction.RIGHT);
				} else if (move == 3) {
					updateParticles(Direction.BACKWARDS);
				} else if (move == 4) {
					updateParticles(Direction.LEFT);
				}

				int f = Integer.parseInt(r.readLine());
				int ri = Integer.parseInt(r.readLine());
				int b = Integer.parseInt(r.readLine());
				int l = Integer.parseInt(r.readLine());

				updateParticles(f, ri, b, l);
			}

			System.out.println("Converged");
			System.out.println(particles.get(0).getLocation().getX() + " - " + particles.get(0).getLocation().getY());
		} catch (Exception e) {
		}
	}

	private boolean isConverged() {
		for (int i = 1; i < particles.size(); i++) {
			if (!particles.get(i).getLocation().equalsTo(particles.get(i - 1).getLocation())) {
				return false;
			}
		}
		return true;
	}

	private void updateParticles(Direction move) {
		ArrayList<Particle> goodParticles = new ArrayList<Particle>();
		ArrayList<Particle> badParticles = new ArrayList<Particle>();

		if (move == Direction.FORWARD) {

			for (Particle p : particles) {

				if (isValidPosition(p.getLocation().getX(), p.getLocation().getY() + 1)) {
					goodParticles.add(new Particle(
							new Location(p.getLocation().getX(), p.getLocation().getY() + 1, LocationType.EMPTY),
							0.0f));
				} else {
					badParticles.add(new Particle(
							new Location(p.getLocation().getX(), p.getLocation().getY(), LocationType.EMPTY), 0.0f));
				}

			}

		} else if (move == Direction.BACKWARDS) {

			for (Particle p : particles) {

				if (isValidPosition(p.getLocation().getX(), p.getLocation().getY() - 1)) {
					goodParticles.add(new Particle(
							new Location(p.getLocation().getX(), p.getLocation().getY() - 1, LocationType.EMPTY),
							0.0f));
				} else {
					badParticles.add(new Particle(
							new Location(p.getLocation().getX(), p.getLocation().getY(), LocationType.EMPTY), 0.0f));
				}

			}

		} else if (move == Direction.RIGHT) {

			for (Particle p : particles) {

				if (isValidPosition(p.getLocation().getX() + 1, p.getLocation().getY())) {
					goodParticles.add(new Particle(
							new Location(p.getLocation().getX() + 1, p.getLocation().getY(), LocationType.EMPTY),
							0.0f));
				} else {
					badParticles.add(new Particle(
							new Location(p.getLocation().getX(), p.getLocation().getY(), LocationType.EMPTY), 0.0f));
				}
			}

		} else if (move == Direction.LEFT) {

			for (Particle p : particles) {

				if (isValidPosition(p.getLocation().getX() - 1, p.getLocation().getY())) {
					goodParticles.add(new Particle(
							new Location(p.getLocation().getX() - 1, p.getLocation().getY(), LocationType.EMPTY),
							0.0f));
				} else {
					badParticles.add(new Particle(
							new Location(p.getLocation().getX(), p.getLocation().getY(), LocationType.EMPTY), 0.0f));
				}
			}

		}

		// Resample

		// Calculate weight for good particles
		for (int i = 0; i < goodParticles.size(); i++) {
			if (goodParticles.get(i).getWeight() == 0.0) {
				int counter = 1;
				for (int j = i + 1; j < goodParticles.size(); j++) {
					if (goodParticles.get(j).getLocation().equalsTo(goodParticles.get(i).getLocation())) {
						counter++;
					}
				}

				float newWeight = (float) counter / (float) goodParticles.size();
				for (int j = i; j < goodParticles.size(); j++) {
					if (goodParticles.get(j).getLocation().equalsTo(goodParticles.get(i).getLocation())) {
						goodParticles.get(j).setWeight(newWeight);
					}
				}
			}
		}

		// Order good particles
		Collections.sort(goodParticles, new Comparator<Particle>() {

			@Override
			public int compare(Particle o1, Particle o2) {
				if (o1.getWeight() < o2.getWeight()) {
					return -1;
				} else if (o1.getWeight() > o2.getWeight()) {
					return 1;
				}
				return 0;
			}
		});

		// Distribute bad particles
		Random generator = new Random();
		//
		Location l;
		float difference = 100.0f;
		Particle closest;
		for (Particle p : badParticles) {

			float number = generator.nextFloat() * 1.0f;
			difference = 100.0f;
			closest = null;

			for (int i = 0; i < goodParticles.size(); i++) {
				if (goodParticles.get(i).getWeight() - number > 0.0f
						&& goodParticles.get(i).getWeight() - number < difference) {
					difference = p.getWeight() - number;
					closest = goodParticles.get(i);
				}
			}

			if (closest == null) {
				closest = goodParticles.get(goodParticles.size() - 1);
			}

			p.setLocation(new Location(closest.getLocation().getX(), closest.getLocation().getY(), LocationType.EMPTY));
		}

		goodParticles.addAll(badParticles);

		for (Particle p : goodParticles) {
			p.setWeight(0.0f);
		}

		particles.clear();
		particles.addAll(goodParticles);
	}

	private void updateParticles(int front, int right, int back, int left) {
		ArrayList<Particle> goodParticles = new ArrayList<Particle>();
		ArrayList<Particle> badParticles = new ArrayList<Particle>();

		for(Particle p : particles) {
			int x = p.getLocation().getX();
			int y = p.getLocation().getY();
			if(isValid(x,y+1) != front && isValid(x, y-1) != back && isValid(x-1, y) != left && isValid(x+1, y) != right) {
				goodParticles.add(new Particle(
						new Location(x, y, LocationType.EMPTY),
						0.0f));
			} else {
				badParticles.add(new Particle(
						new Location(x, y, LocationType.EMPTY), 0.0f));
			}
		}

		// Resample

		// Calculate weight for good particles
		for (int i = 0; i < goodParticles.size(); i++) {
			if (goodParticles.get(i).getWeight() == 0.0) {
				int counter = 1;
				for (int j = i + 1; j < goodParticles.size(); j++) {
					if (goodParticles.get(j).getLocation().equalsTo(goodParticles.get(i).getLocation())) {
						counter++;
					}
				}

				float newWeight = (float) counter / (float) goodParticles.size();
				for (int j = i; j < goodParticles.size(); j++) {
					if (goodParticles.get(j).getLocation().equalsTo(goodParticles.get(i).getLocation())) {
						goodParticles.get(j).setWeight(newWeight);
					}
				}
			}
		}

		// Order good particles
		Collections.sort(goodParticles, new Comparator<Particle>() {

			@Override
			public int compare(Particle o1, Particle o2) {
				if (o1.getWeight() < o2.getWeight()) {
					return -1;
				} else if (o1.getWeight() > o2.getWeight()) {
					return 1;
				}
				return 0;
			}
		});

		// Distribute bad particles
		Random generator = new Random();
		//
		Location l;
		float difference = 100.0f;
		Particle closest;
		for (Particle p : badParticles) {

			float number = generator.nextFloat() * 1.0f;
			difference = 100.0f;
			closest = null;

			for (int i = 0; i < goodParticles.size(); i++) {
				if (goodParticles.get(i).getWeight() - number > 0.0f
						&& goodParticles.get(i).getWeight() - number < difference) {
					difference = p.getWeight() - number;
					closest = goodParticles.get(i);
				}
			}

			if (closest == null) {
				closest = goodParticles.get(goodParticles.size() - 1);
			}

			p.setLocation(new Location(closest.getLocation().getX(), closest.getLocation().getY(), LocationType.EMPTY));
		}

		goodParticles.addAll(badParticles);

		for (Particle p : goodParticles) {
			p.setWeight(0.0f);
		}

		particles.clear();
		particles.addAll(goodParticles);
	}

	public int isValidBehind(int x, int y) {
		if (y - 1 >= 0) {
			if (map[x][y - 1].getType() == LocationType.BLOCK) {
				return 0;
			} else {
				return 1;
			}
		} else {
			return 0;
		}
	}

	public int isValidInFront(int x, int y) {
		if (y + 1 < maxY) {
			if (map[x][y + 1].getType() == LocationType.BLOCK) {
				return 0;
			} else {
				return 1;
			}
		} else {
			return 0;
		}
	}

	public int isValidLeft(int x, int y) {
		if (x - 1 >= 0) {
			if (map[x - 1][y].getType() == LocationType.BLOCK) {
				return 0;
			} else {
				return 1;
			}
		} else {
			return 0;
		}
	}

	public int isValidRight(int x, int y) {
		if (x + 1 < maxX) {
			if (map[x + 1][y].getType() == LocationType.BLOCK) {
				return 0;
			} else {
				return 1;
			}
		} else {
			return 0;
		}
	}

	public boolean isValidPosition(int x, int y) {
		if (x < 0 || y < 0 || x >= maxX || y >= maxY) {
			return false;
		} else if (map[x][y].getType() == LocationType.BLOCK) {
			return false;
		}
		return true;
	}

	public int isValid(int x, int y) {
		if (x < 0 || y < 0 || x >= maxX || y >= maxY) {
			return 0;
		} else if (map[x][y].getType() == LocationType.BLOCK) {
			return 0;
		}
		return 1;
	}

	private void createMap() {
		maxX = 12;
		maxY = 8;
		map = new Location[maxX][maxY];
		for (int i = 0; i < maxX; i++) {
			for (int j = 0; j < maxY; j++) {
				map[i][j] = new Location(i, j, LocationType.EMPTY);
			}
		}

		map[1][1].setType(LocationType.BLOCK);
		map[1][2].setType(LocationType.BLOCK);
		map[1][3].setType(LocationType.BLOCK);
		map[1][4].setType(LocationType.BLOCK);
		map[1][5].setType(LocationType.BLOCK);

		map[4][1].setType(LocationType.BLOCK);
		map[4][2].setType(LocationType.BLOCK);
		map[4][3].setType(LocationType.BLOCK);
		map[4][4].setType(LocationType.BLOCK);
		map[4][5].setType(LocationType.BLOCK);

		map[7][1].setType(LocationType.BLOCK);
		map[7][2].setType(LocationType.BLOCK);
		map[7][3].setType(LocationType.BLOCK);
		map[7][4].setType(LocationType.BLOCK);
		map[7][5].setType(LocationType.BLOCK);

		map[10][1].setType(LocationType.BLOCK);
		map[10][2].setType(LocationType.BLOCK);
		map[10][3].setType(LocationType.BLOCK);
		map[10][4].setType(LocationType.BLOCK);
		map[10][5].setType(LocationType.BLOCK);
	}

	private void createParticles() {
		NUMBER_OF_PARTICLES = 0;
		particles = new ArrayList<Particle>();

		for (int i = 0; i < maxX; i++) {
			for (int j = 0; j < maxY; j++) {
				if (map[i][j].getType() == LocationType.EMPTY) {
					particles.add(new Particle(new Location(i, j, LocationType.EMPTY), 0.0f));
					NUMBER_OF_PARTICLES++;
				}
			}
		}

	}

}

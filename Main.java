import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static List<User> users = new ArrayList<>();
    private static List<Event> events = new ArrayList<>();
    private static final String USERS_FILE = "users.data";
    private static final String EVENTS_FILE = "events.data";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static User loggedInUser = null;

    public static void main(String[] args) {
        loadUsersFromFile();
        loadEventsFromFile();

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                displayMainMenu();
                int option = scanner.nextInt();
                scanner.nextLine(); // Limpar o buffer

                if (loggedInUser == null) {
                    handleUserOptions(scanner, option);
                } else {
                    handleLoggedInUserOptions(scanner, option);
                }
            }
        }
    }

    private static void displayMainMenu() {
        System.out.println("======= Sistema de Eventos =======");
        if (loggedInUser == null) {
            System.out.println("1. Cadastrar usuário");
            System.out.println("2. Fazer login");
        } else {
            System.out.println("1. Cadastrar evento");
            System.out.println("2. Consultar eventos");
            System.out.println("3. Participar de evento");
            System.out.println("4. Cancelar participação em evento");
            System.out.println("5. Fazer logout");
        }
        System.out.println("0. Sair");
        System.out.print("Digite a opção desejada: ");
    }

    private static void handleUserOptions(Scanner scanner, int option) {
        switch (option) {
            case 1:
                cadastrarUsuario(scanner);
                break;
            case 2:
                fazerLogin(scanner);
                break;
            case 0:
                saveUsersToFile();
                saveEventsToFile();
                System.exit(0);
            default:
                System.out.println("Opção inválida");
                break;
        }
    }

    private static void handleLoggedInUserOptions(Scanner scanner, int option) {
        switch (option) {
            case 1:
                cadastrarEvento(scanner);
                break;
            case 2:
                consultarEventos();
                break;
            case 3:
                participarEvento(scanner);
                break;
            case 4:
                cancelarParticipacaoEvento(scanner);
                break;
            case 5:
                fazerLogout();
                break;
            case 0:
                saveUsersToFile();
                saveEventsToFile();
                System.exit(0);
            default:
                System.out.println("Opção inválida");
                break;
        }
    }

    private static void cadastrarUsuario(Scanner scanner) {
        System.out.print("Nome: ");
        String name = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Senha: ");
        String password = scanner.nextLine();
        System.out.print("Idade: ");
        int age = scanner.nextInt();
        scanner.nextLine();

        if (User.isEmailRegistered(users, email)) {
            System.out.println("Falha no cadastro. O email já está registrado.");
        } else {
            users.add(new User(name, email, password, age));
            System.out.println("Usuário cadastrado com sucesso!");
        }
        System.out.println();
    }

    private static void fazerLogin(Scanner scanner) {
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Senha: ");
        String password = scanner.nextLine();

        User user = User.getUserByEmail(users, email);
        if (user != null && user.getPassword().equals(password)) {
            loggedInUser = user;
            System.out.println("Login bem-sucedido!");
        } else {
            System.out.println("Email ou senha inválidos. Tente novamente.");
        }
        System.out.println();
    }

    private static void fazerLogout() {
        loggedInUser = null;
        System.out.println("Logout realizado com sucesso!");
        System.out.println();
    }

    private static void cadastrarEvento(Scanner scanner) {
        System.out.print("Nome do evento: ");
        String name = scanner.nextLine();
        System.out.print("Data e hora do evento (formato: yyyy-MM-dd HH:mm): ");
        LocalDateTime dateTime = LocalDateTime.parse(scanner.nextLine(), DATE_TIME_FORMATTER);
        System.out.print("Local do evento: ");
        String location = scanner.nextLine();
        System.out.print("Descrição do evento: ");
        String description = scanner.nextLine();

        Event event = new Event(name, dateTime, location, description);
        events.add(event);
        System.out.println("Evento cadastrado com sucesso!");
        System.out.println();
    }

    private static void consultarEventos() {
        System.out.println("======= Eventos Disponíveis =======");
        for (Event event : events) {
            System.out.println("Nome: " + event.getName());
            System.out.println("Data e Hora: " + event.getDateTime().format(DATE_TIME_FORMATTER));
            System.out.println("Local: " + event.getLocation());
            System.out.println("Descrição: " + event.getDescription());
            System.out.println("------------------------------");
        }
        System.out.println();
    }

    private static void participarEvento(Scanner scanner) {
        System.out.print("Digite o nome do evento que deseja participar: ");
        String eventName = scanner.nextLine();

        Event event = Event.getEventByName(events, eventName);
        if (event != null) {
            if (event.addParticipant(loggedInUser)) {
                System.out.println("Você está participando do evento: " + event.getName());
            } else {
                System.out.println("Não foi possível participar do evento. Limite de participantes atingido.");
            }
        } else {
            System.out.println("Evento não encontrado.");
        }
        System.out.println();
    }

    private static void cancelarParticipacaoEvento(Scanner scanner) {
        System.out.print("Digite o nome do evento do qual deseja cancelar a participação: ");
        String eventName = scanner.nextLine();

        Event event = Event.getEventByName(events, eventName);
        if (event != null) {
            if (event.removeParticipant(loggedInUser)) {
                System.out.println("Você cancelou a participação no evento: " + event.getName());
            } else {
                System.out.println("Você não estava participando desse evento.");
            }
        } else {
            System.out.println("Evento não encontrado.");
        }
        System.out.println();
    }

    private static void loadUsersFromFile() {
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Erro ao criar o arquivo de usuários.");
            }
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");
                users.add(new User(userData[0], userData[1], userData[2], Integer.parseInt(userData[3])));
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo de usuários.");
        }
    }

    private static void loadEventsFromFile() {
        File file = new File(EVENTS_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Erro ao criar o arquivo de eventos.");
            }
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] eventData = line.split(",");
                LocalDateTime dateTime = LocalDateTime.parse(eventData[1], DATE_TIME_FORMATTER);
                events.add(new Event(eventData[0], dateTime, eventData[2], eventData[3]));
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo de eventos.");
        }
    }

    private static void saveUsersToFile() {
        try (PrintWriter writer = new PrintWriter(new File(USERS_FILE))) {
            for (User user : users) {
                writer.println(user.getName() + "," + user.getEmail() + "," + user.getPassword() + "," + user.getAge());
            }
        } catch (FileNotFoundException e) {
            System.out.println("Erro ao salvar arquivo de usuários.");
        }
    }

    private static void saveEventsToFile() {
        try (PrintWriter writer = new PrintWriter(new File(EVENTS_FILE))) {
            for (Event event : events) {
                writer.println(event.getName() + "," + event.getDateTime().format(DATE_TIME_FORMATTER) + "," + event.getLocation() + "," + event.getDescription());
            }
        } catch (FileNotFoundException e) {
            System.out.println("Erro ao salvar arquivo de eventos.");
        }
    }
}

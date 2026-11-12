import java.util.*;

/* ---------- Main program (CLI) ---------- */
public class PhoneBook {
    static ContactBST bst = new ContactBST();
    static PhoneHashTable table = new PhoneHashTable();
    static Scanner sc = new Scanner(System.in);

    static boolean validPhone(String p) { return p.matches("\\d{7,15}"); }

    static void add(String name, String phone, String email, boolean verbose) {
        if (name.isEmpty()) { System.out.println("Name cannot be empty."); return; }
        if (!validPhone(phone)) { System.out.println("Phone must be 7-15 digits."); return; }
        if (bst.search(name) != null) { System.out.println("A contact named '" + name + "' already exists."); return; }
        if (table.search(phone) != null) { System.out.println("That phone number already exists."); return; }
        Contact c = new Contact(name, phone, email);
        bst.insert(c);
        table.insert(c);
        if (verbose) System.out.println("Contact added.");
    }

    static void addContact() {
        System.out.print("Name  : "); String n = sc.nextLine().trim();
        System.out.print("Phone : "); String p = sc.nextLine().trim();
        System.out.print("Email : "); String e = sc.nextLine().trim();
        add(n, p, e, true);
    }

    static void searchByName() {
        System.out.print("Enter name: ");
        Contact c = bst.search(sc.nextLine().trim());
        System.out.println(c == null ? "Not found." : "Found: " + c);
        System.out.println("(BST comparisons: " + bst.lastComparisons + ")");
    }

    static void searchByPhone() {
        System.out.print("Enter phone: ");
        Contact c = table.search(sc.nextLine().trim());
        System.out.println(c == null ? "Not found." : "Found: " + c);
        System.out.println("(Hash probes: " + table.lastProbes + ")");
    }

    static void prefixSearch() {
        System.out.print("Enter name prefix: ");
        List<Contact> res = bst.prefixSearch(sc.nextLine().trim());
        if (res.isEmpty()) System.out.println("No matches.");
        else res.forEach(System.out::println);
    }

    static void deleteContact() {
        System.out.print("Enter name to delete: ");
        Contact c = bst.search(sc.nextLine().trim());
        if (c == null) { System.out.println("Not found."); return; }
        bst.delete(c.name);
        table.delete(c.phone);
        System.out.println("Deleted: " + c.name);
    }

    /* Edit: the name is the BST key, so only phone and email can be changed.
       Press Enter on a field to keep its old value. */
    static void editContact() {
        System.out.print("Enter name of contact to edit: ");
        Contact c = bst.search(sc.nextLine().trim());
        if (c == null) { System.out.println("Not found."); return; }
        System.out.println("Current: " + c);

        System.out.print("New phone (Enter to keep): ");
        String p = sc.nextLine().trim();
        System.out.print("New email (Enter to keep): ");
        String e = sc.nextLine().trim();

        if (!p.isEmpty() && !p.equals(c.phone)) {
            if (!validPhone(p)) { System.out.println("Phone must be 7-15 digits. Nothing changed."); return; }
            if (table.search(p) != null) { System.out.println("That phone number already exists. Nothing changed."); return; }
            table.delete(c.phone);      // remove old phone from hash table
            c.phone = p;
            table.insert(c);            // insert under the new phone
        }
        if (!e.isEmpty()) c.email = e;  // same object is shared by BST and hash table
        System.out.println("Updated: " + c);
    }

    static void listByName() {
        List<Contact> list = bst.inorder();      // in-order traversal = sorted by name
        if (list.isEmpty()) { System.out.println("Phonebook is empty."); return; }
        System.out.printf("%-20s %-15s %s%n", "NAME", "PHONE", "EMAIL");
        list.forEach(System.out::println);
    }

    static void listByPhone() {
        Contact[] arr = bst.inorder().toArray(new Contact[0]);
        if (arr.length == 0) { System.out.println("Phonebook is empty."); return; }
        Sorter.mergeSort(arr, Comparator.comparing(c -> c.phone));
        System.out.printf("%-20s %-15s %s%n", "NAME", "PHONE", "EMAIL");
        for (Contact c : arr) System.out.println(c);
    }

    static void stats() {
        System.out.println("Total contacts     : " + bst.size());
        System.out.println("BST height         : " + bst.height());
        System.out.println("Hash table capacity: " + table.capacity());
        System.out.printf("Load factor        : %.2f%n", table.loadFactor());
        System.out.println("--- Hash table layout ---");
        table.showTable();
    }

    static void loadSample() {
        String[][] d = {
            {"Mohan", "9811111111", "mohan@mail.com"},
            {"Amit", "9822222222", "amit@mail.com"},
            {"Zara", "9833333333", "zara@mail.com"},
            {"Bhavna", "9844444444", "bhavna@mail.com"},
            {"Rohit", "9855555555", "rohit@mail.com"},
            {"Neha", "9866666666", "neha@mail.com"},
            {"Suresh", "9877777777", "suresh@mail.com"}
        };
        for (String[] r : d) add(r[0], r[1], r[2], false);
        System.out.println("Sample contacts loaded.");
    }

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n===== PHONEBOOK MANAGEMENT =====");
            System.out.println("1.  Add contact");
            System.out.println("2.  Search by name (BST)");
            System.out.println("3.  Search by phone (Hash table)");
            System.out.println("4.  Search by name prefix");
            System.out.println("5.  Delete contact");
            System.out.println("6.  Edit contact");
            System.out.println("7.  Display sorted by name (in-order)");
            System.out.println("8.  Display sorted by phone (merge sort)");
            System.out.println("9.  Statistics");
            System.out.println("10. Load sample data");
            System.out.println("0.  Exit");
            System.out.print("Choice: ");
            if (!sc.hasNextLine()) return;
            String ch = sc.nextLine().trim();
            switch (ch) {
                case "1": addContact(); break;
                case "2": searchByName(); break;
                case "3": searchByPhone(); break;
                case "4": prefixSearch(); break;
                case "5": deleteContact(); break;
                case "6": editContact(); break;
                case "7": listByName(); break;
                case "8": listByPhone(); break;
                case "9": stats(); break;
                case "10": loadSample(); break;
                case "0": System.out.println("Goodbye!"); return;
                default: System.out.println("Invalid choice.");
            }
        }
    }
}

/* ---------- Data record ---------- */
class Contact {
    String name, phone, email;

    Contact(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    String key() { return name.toLowerCase(); }   // case-insensitive BST key

    public String toString() {
        return String.format("%-20s %-15s %s", name, phone, email);
    }
}

/* ---------- Module 1: Binary Search Tree (keyed by name) ---------- */
class ContactBST {
    private class Node {
        Contact c; Node left, right;
        Node(Contact c) { this.c = c; }
    }

    private Node root;
    private int size;
    private boolean found;
    int lastComparisons;

    int size() { return size; }

    boolean insert(Contact c) {
        lastComparisons = 0;
        Node parent = null, cur = root;
        while (cur != null) {
            lastComparisons++;
            int cmp = c.key().compareTo(cur.c.key());
            if (cmp == 0) return false;               // duplicate name
            parent = cur;
            cur = (cmp < 0) ? cur.left : cur.right;
        }
        Node n = new Node(c);
        if (parent == null) root = n;
        else if (c.key().compareTo(parent.c.key()) < 0) parent.left = n;
        else parent.right = n;
        size++;
        return true;
    }

    Contact search(String name) {
        lastComparisons = 0;
        String k = name.toLowerCase();
        Node cur = root;
        while (cur != null) {
            lastComparisons++;
            int cmp = k.compareTo(cur.c.key());
            if (cmp == 0) return cur.c;
            cur = (cmp < 0) ? cur.left : cur.right;
        }
        return null;
    }

    boolean delete(String name) {
        found = false;
        root = delete(root, name.toLowerCase());
        if (found) size--;
        return found;
    }

    private Node delete(Node n, String k) {
        if (n == null) return null;
        int cmp = k.compareTo(n.c.key());
        if (cmp < 0) n.left = delete(n.left, k);
        else if (cmp > 0) n.right = delete(n.right, k);
        else {
            found = true;
            if (n.left == null) return n.right;       // 0 or 1 child
            if (n.right == null) return n.left;
            Node s = n.right;                          // 2 children: in-order successor
            while (s.left != null) s = s.left;
            n.c = s.c;
            n.right = delete(n.right, s.c.key());
        }
        return n;
    }

    List<Contact> inorder() {
        List<Contact> out = new ArrayList<>();
        inorder(root, out);
        return out;
    }

    private void inorder(Node n, List<Contact> out) {
        if (n == null) return;
        inorder(n.left, out);
        out.add(n.c);
        inorder(n.right, out);
    }

    List<Contact> prefixSearch(String prefix) {
        List<Contact> out = new ArrayList<>();
        collect(root, prefix.toLowerCase(), out);
        return out;
    }

    private void collect(Node n, String p, List<Contact> out) {
        if (n == null) return;
        String k = n.c.key();
        if (p.compareTo(k) <= 0) collect(n.left, p, out);
        if (k.startsWith(p)) out.add(n.c);
        if (k.compareTo(p) < 0 || k.startsWith(p)) collect(n.right, p, out);
    }

    int height() { return height(root); }

    private int height(Node n) {
        return n == null ? 0 : 1 + Math.max(height(n.left), height(n.right));
    }
}

/* ---------- Module 2: Hash table (keyed by phone, linear probing) ---------- */
class PhoneHashTable {
    private Contact[] slots;
    private boolean[] deleted;
    private int count, tombstones;
    int lastProbes;

    PhoneHashTable() { init(11); }

    private void init(int cap) {
        slots = new Contact[cap];
        deleted = new boolean[cap];
        count = 0;
        tombstones = 0;
    }

    int capacity() { return slots.length; }
    double loadFactor() { return (double) count / slots.length; }

    private int hash(String phone) {
        long h = 0;
        for (int i = 0; i < phone.length(); i++)
            h = (h * 31 + phone.charAt(i)) % slots.length;
        return (int) h;
    }

    private int find(String phone) {
        int start = hash(phone);
        lastProbes = 0;
        while (lastProbes < slots.length) {
            int i = (start + lastProbes) % slots.length;
            lastProbes++;
            if (slots[i] == null && !deleted[i]) return -1;   // truly empty: stop
            if (slots[i] != null && slots[i].phone.equals(phone)) return i;
        }
        return -1;
    }

    Contact search(String phone) {
        int i = find(phone);
        return i == -1 ? null : slots[i];
    }

    boolean insert(Contact c) {
        if (find(c.phone) != -1) return false;                // duplicate number
        if (count + tombstones + 1 > slots.length / 2) rehash();
        int start = hash(c.phone);
        lastProbes = 0;
        while (true) {
            int i = (start + lastProbes) % slots.length;
            lastProbes++;
            if (slots[i] == null) {
                if (deleted[i]) { deleted[i] = false; tombstones--; }
                slots[i] = c;
                count++;
                return true;
            }
        }
    }

    boolean delete(String phone) {
        int i = find(phone);
        if (i == -1) return false;
        slots[i] = null;
        deleted[i] = true;                                    // tombstone
        count--;
        tombstones++;
        return true;
    }

    private void rehash() {
        Contact[] old = slots;
        int newCap = nextPrime(old.length * 2);
        init(newCap);
        for (Contact c : old) if (c != null) insert(c);
    }

    private int nextPrime(int n) {
        while (true) {
            boolean prime = n > 1;
            for (int d = 2; d * d <= n; d++) if (n % d == 0) { prime = false; break; }
            if (prime) return n;
            n++;
        }
    }

    void showTable() {
        for (int i = 0; i < slots.length; i++)
            System.out.printf("[%2d] %s%n", i,
                    slots[i] != null ? slots[i].phone + " -> " + slots[i].name
                                     : (deleted[i] ? "<deleted>" : "<empty>"));
    }
}

/* ---------- Module 3: Merge sort (sort by any field) ---------- */
class Sorter {
    static void mergeSort(Contact[] a, Comparator<Contact> cmp) {
        if (a.length < 2) return;
        Contact[] l = Arrays.copyOfRange(a, 0, a.length / 2);
        Contact[] r = Arrays.copyOfRange(a, a.length / 2, a.length);
        mergeSort(l, cmp);
        mergeSort(r, cmp);
        int i = 0, j = 0, k = 0;
        while (i < l.length && j < r.length)
            a[k++] = (cmp.compare(l[i], r[j]) <= 0) ? l[i++] : r[j++];
        while (i < l.length) a[k++] = l[i++];
        while (j < r.length) a[k++] = r[j++];
    }
}
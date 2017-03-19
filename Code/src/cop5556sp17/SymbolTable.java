package cop5556sp17;


import cop5556sp17.AST.Dec;

import java.util.*;


public class SymbolTable {
    private Map<String, List<SymbolTableEntry>> symbolTable;
    // Deque is used because iterating through Stack does not follow LIFO order
    // Also Deque is more recommended as compared to Stack, as mentioned in the documentation
    private Deque<Integer> scopeStack;
    private Integer globalScopeLevel;
    private Integer currentScopeLevel;

    public SymbolTable() {
        this.symbolTable = new HashMap<>();
        this.scopeStack = new ArrayDeque<>();
        this.globalScopeLevel = 0;

        enterScope(); // Puts scope level 0 onto the stack, which helps in storing top-level variables such as paramDecs
    }

    /**
     * to be called when block entered
     */
    public void enterScope() {
        currentScopeLevel = globalScopeLevel++;
        scopeStack.push(currentScopeLevel);
    }


    /**
     * leaves scope
     */
    public void leaveScope() {
        scopeStack.pop();
        currentScopeLevel = scopeStack.peek();
    }

    public boolean insert(String ident, Dec dec) {
        Objects.requireNonNull(ident, "SymbolTable.insert :: Identifier can't be null");
        Objects.requireNonNull(dec, "SymbolTable.insert :: Dec can't be null");

        if (symbolTable.containsKey(ident)) {
            List<SymbolTableEntry> symbolTableEntries = symbolTable.get(ident);
            SymbolTableEntry existingSymbolTableEntry = lookupSymbolTableEntry(ident);
            if (existingSymbolTableEntry != null && existingSymbolTableEntry.getScopeLevel().equals(currentScopeLevel)) {
                return false;
            } else {
                symbolTableEntries.add(0, new SymbolTableEntry(currentScopeLevel, dec));
                return true;
            }
        } else {
            SymbolTableEntry symbolTableEntry = new SymbolTableEntry(currentScopeLevel, dec);
            List<SymbolTableEntry> symbolTableEntries = new LinkedList<>();
            symbolTableEntries.add(0, symbolTableEntry);
            symbolTable.put(ident, symbolTableEntries);
            return true;
        }
    }

    public Dec lookup(String ident) {
        Objects.requireNonNull(ident, "SymbolTable.lookup :: Identifier can't be null");

        SymbolTableEntry symbolTableEntry = lookupSymbolTableEntry(ident);
        if (symbolTableEntry != null) {
            return symbolTableEntry.getDec();
        } else {
            return null;
        }
    }

    private SymbolTableEntry lookupSymbolTableEntry(String ident) {
        Objects.requireNonNull(ident, "SymbolTable :: Identifier can't be null");

        if (symbolTable.containsKey(ident)) {
            List<SymbolTableEntry> symbolTableEntries = symbolTable.get(ident);
            Iterator<SymbolTableEntry> symbolTableEntryIterator = symbolTableEntries.iterator();
            Iterator<Integer> scopeStackIterator = scopeStack.iterator();

            if (scopeStackIterator.hasNext() && symbolTableEntryIterator.hasNext()) {
                Integer scopeLevel = scopeStackIterator.next();
                SymbolTableEntry symbolTableEntry = symbolTableEntryIterator.next();

                while (scopeLevel != null && symbolTableEntry != null) {
                    int compareResult = symbolTableEntry.getScopeLevel().compareTo(scopeLevel);

                    if (compareResult == 0) {
                        return symbolTableEntry;
                    } else if (compareResult > 0) {
                        if (symbolTableEntryIterator.hasNext()) {
                            symbolTableEntry = symbolTableEntryIterator.next();
                        } else {
                            symbolTableEntry = null;
                        }
                    } else {
                        if (scopeStackIterator.hasNext()) {
                            scopeLevel = scopeStackIterator.next();
                        } else {
                            scopeLevel = null;
                        }
                    }
                }
            }

            return null;
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "SymbolTable{" +
//                "symbolTable=" + symbolTable +
                ", symbolTableSize=" + symbolTable.size() +
                ", scopeStack=" + scopeStack +
                ", scopeStackSize=" + scopeStack.size() +
                ", globalScopeLevel=" + globalScopeLevel +
                '}';
    }

    private class SymbolTableEntry {
        Integer scopeLevel;
        Dec dec;

        public SymbolTableEntry(Integer scopeLevel, Dec dec) {
            this.scopeLevel = scopeLevel;
            this.dec = dec;
        }

        public Integer getScopeLevel() {
            return scopeLevel;
        }

        public void setScopeLevel(Integer scopeLevel) {
            this.scopeLevel = scopeLevel;
        }

        public Dec getDec() {
            return dec;
        }

        public void setDec(Dec dec) {
            this.dec = dec;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SymbolTableEntry that = (SymbolTableEntry) o;

            if (!scopeLevel.equals(that.scopeLevel)) return false;
            return dec.equals(that.dec);
        }

        @Override
        public int hashCode() {
            int result = scopeLevel.hashCode();
            result = 31 * result + dec.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "SymbolTableEntry{" +
                    "scopeLevel=" + scopeLevel +
                    ", dec=" + dec +
                    '}';
        }
    }
}

package codechicken.nei.recipe;

import codechicken.nei.Button;
import codechicken.nei.NEIClientConfig;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class GuiRecipeTabs {
    private final GuiRecipe guiRecipe;
    private final List<GuiRecipeTab> tabs = new ArrayList<>();
    private final List<Button> buttons = new ArrayList<>();

    private final Rectangle area = new Rectangle();
    
    private int pageCount = 1;
    private int pageNumber = 0;
    private int categoriesPerPage = 1;
    private int numHandlers = 1;

    public GuiRecipeTabs(GuiRecipe guiRecipe) {
        this.guiRecipe = guiRecipe;
    }
    
    public void initLayout() {
        int totalWidth = 0;
        categoriesPerPage = 0;
        numHandlers = guiRecipe.currenthandlers.size();
        for (IRecipeHandler handler : guiRecipe.currenthandlers) {
            if (totalWidth + GuiRecipeTab.TAB_WIDTH < (guiRecipe.xSize - 4)) {
                totalWidth += GuiRecipeTab.TAB_WIDTH;
                categoriesPerPage++;
            } else {
                break;
            }
        }

        area.width = totalWidth;
        area.height = GuiRecipeTab.TAB_HEIGHT;
        area.x = guiRecipe.guiLeft + 2;
        area.y = guiRecipe.guiTop - GuiRecipeTab.TAB_HEIGHT + 3;

        pageCount = (int) Math.ceil((float) numHandlers / categoriesPerPage);
        calcPageNumber();
        Rectangle navigationArea = new Rectangle(area);
        navigationArea.height = 20;
        navigationArea.translate(0, -(2 + navigationArea.height));
    }
    
    public void calcPageNumber() {
        pageNumber = guiRecipe.recipetype / Math.max(categoriesPerPage, 1);
    }
    
    public void refreshPage() {
        tabs.clear();
        buttons.clear();
        
        int tabX = area.x;

        final int startIndex = pageNumber * categoriesPerPage;
        for (int i = 0 ; i < categoriesPerPage ; i++) {
            final int index = i + startIndex;
            if (index >= numHandlers) break;
            IRecipeHandler handler = guiRecipe.currenthandlers.get(index);
            tabs.add(new GuiRecipeTab(guiRecipe, handler, tabX, area.y));
            tabX += GuiRecipeTab.TAB_WIDTH;
        }
        
        // Maybe add buttons
        if (numHandlers > categoriesPerPage) {
            Button prevTab = new Button("prevTab") {
                @Override
                public boolean onButtonPress(boolean rightclick) {
                    if (!rightclick) return previousPage();
                    else             return false;
                }
                @Override
                public String getRenderLabel() {
                    return "<";
                }
            };
            prevTab.x = area.x - 7;
            prevTab.y = area.y + 2;
            prevTab.h = 20;
            prevTab.w = 8;
            buttons.add(prevTab);
            
            Button nextTab = new Button("nextTab") {
                @Override
                public boolean onButtonPress(boolean rightclick) {
                    if(!rightclick) return nextPage();
                    else            return false;
                }
                @Override
                public String getRenderLabel() {
                    return ">";
                }
            };
            nextTab.x = area.x + area.width;
            nextTab.y = area.y + 2;
            nextTab.h = 20;
            nextTab.w = 8;
            buttons.add(nextTab);
        }
    }
    
    public boolean nextPage() {
        if (hasNext()) pageNumber++; 
        else pageNumber = 0;
        
        refreshPage();
        return true;
    }

    public boolean hasNext() {
        return pageNumber + 1 < pageCount;
    }

    public boolean previousPage() {
        if (hasPrevious()) pageNumber--;
        else pageNumber = pageCount - 1;
        
        refreshPage();
        return true;
    }

    public boolean hasPrevious() {
        return pageNumber > 0;
    }


    public void draw(int mouseX, int mouseY) {
        IRecipeHandler current = guiRecipe.currenthandlers.get(guiRecipe.recipetype);
        for (GuiRecipeTab tab : tabs) {
            boolean selected = tab.isSelected(current);
            tab.draw(selected, mouseX, mouseY);
        }
        for (Button button : buttons) {
            button.draw(mouseX, mouseY);
        }
    }

    public void handleTooltip(int mousex, int mousey, List<String> tooltips) {
        if (!NEIClientConfig.areJEIStyleTabsVisible()) return;

        for (GuiRecipeTab tab : tabs) {
            if (tab.contains(mousex, mousey)) {
                tab.addTooltips(tooltips);
            }
        }
        for (Button button : buttons) {
            if (button.contains(mousex, mousey)){
                button.addTooltips(tooltips);
            }
        }
    }
    protected boolean mouseClicked(int x, int y, int mouseButton) {
        if (!NEIClientConfig.areJEIStyleTabsVisible()) return false;

        for (Button button : buttons) {
            if (button.contains(x, y)) {
                return button.onButtonPress(mouseButton == 1);
            }
        }
        for (GuiRecipeTab tab : tabs) {
            if (tab.contains(x, y)) {
                return tab.onButtonPress(mouseButton == 1);
            }
        }
        return false;
    }
    
}
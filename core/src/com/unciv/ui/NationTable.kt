package com.unciv.ui

import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.unciv.GameParameters
import com.unciv.models.gamebasics.GameBasics
import com.unciv.models.gamebasics.Nation
import com.unciv.models.gamebasics.Translations
import com.unciv.models.gamebasics.tr
import com.unciv.ui.utils.ImageGetter
import com.unciv.ui.utils.onClick
import com.unciv.ui.utils.setFontColor
import com.unciv.ui.utils.toLabel

class NationTable(val nation: Nation, val newGameParameters: GameParameters, skin: Skin, width:Float, onClick:()->Unit): Table(skin){
    val innerTable = Table()
    init {
        background= ImageGetter.getBackground(nation.getSecondaryColor())
        innerTable.pad(10f)
        innerTable.background= ImageGetter.getBackground(nation.getColor())
        innerTable.add(Label(nation.leaderName.tr()+" - "+nation.name.tr(), skin)
                .apply { setFontColor(nation.getSecondaryColor())}).row()
        innerTable.add(getUniqueLabel(nation)
                .apply { setWrap(true);setFontColor(nation.getSecondaryColor())})
                .width(width)
        onClick {
            if (nation.name in newGameParameters.humanNations) {
                newGameParameters.humanNations.remove(nation.name)
            } else {
                newGameParameters.humanNations.add(nation.name)
                if (newGameParameters.humanNations.size > newGameParameters.numberOfHumanPlayers)
                    newGameParameters.humanNations.removeAt(0)
            }
            onClick()
        }
        touchable= Touchable.enabled
        add(innerTable)
    }

    private fun getUniqueLabel(nation: Nation): Label {
        val textList = ArrayList<String>()

        if(nation.unique!=null) {
            textList += nation.unique!!.tr()
            textList += ""
        }

        for (building in GameBasics.Buildings.values)
            if (building.uniqueTo == nation.name) {
                val originalBuilding = GameBasics.Buildings[building.replaces!!]!!

                textList += building.name.tr() + " - {replaces} " + originalBuilding.name.tr()
                val originalBuildingStatMap = originalBuilding.toHashMap()
                for (stat in building.toHashMap())
                    if (stat.value != originalBuildingStatMap[stat.key])
                        textList += "  "+stat.key.toString().tr() +" "+stat.value.toInt() + " vs " + originalBuildingStatMap[stat.key]!!.toInt()

                for(unique in building.uniques.filter { it !in originalBuilding.uniques })
                    textList += "  "+unique.tr()
                if (building.maintenance != originalBuilding.maintenance)
                    textList += "  {Maintenance} " + building.maintenance + " vs " + originalBuilding.maintenance
                if(building.cost != originalBuilding.cost)
                    textList += "  {Cost} " + building.cost + " vs " + originalBuilding.cost
                if(building.cityStrength != originalBuilding.cityStrength)
                    textList += "  {City strength} " + building.cityStrength+ " vs " + originalBuilding.cityStrength
                if(building.cityHealth!= originalBuilding.cityHealth)
                    textList += "  {City health} " + building.cityHealth+ " vs " + originalBuilding.cityHealth
                textList+=""
            }

        for (unit in GameBasics.Units.values)
            if (unit.uniqueTo == nation.name) {
                val originalUnit = GameBasics.Units[unit.replaces!!]!!

                textList += unit.name.tr() + " - {replaces} " + originalUnit.name.tr()
                if(unit.cost != originalUnit.cost)
                    textList += "  {Cost} " + unit.cost + " vs " + originalUnit.cost
                if (unit.strength != originalUnit.strength)
                    textList += "  {Strength} " + unit.strength + " vs " + originalUnit.strength
                if (unit.rangedStrength!= originalUnit.rangedStrength)
                    textList+= "  {Ranged strength} " + unit.rangedStrength+ " vs " + originalUnit.rangedStrength
                if (unit.range!= originalUnit.range)
                    textList+= "  {Range} " + unit.range+ " vs " + originalUnit.range
                if (unit.movement!= originalUnit.movement)
                    textList+= "  {Movement} " + unit.movement+ " vs " + originalUnit.movement
                if(originalUnit.requiredResource!=null && unit.requiredResource==null)
                    textList+= "  "+"[${originalUnit.requiredResource}] not required".tr()
                for(unique in unit.uniques.filterNot { it in originalUnit.uniques })
                    textList += "  "+Translations.translateBonusOrPenalty(unique)
                for(promotion in unit.promotions.filter { it !in originalUnit.promotions})
                    textList += "  "+promotion.tr()+ " ("+Translations.translateBonusOrPenalty(GameBasics.UnitPromotions[promotion]!!.effect)+")"
                textList+=""
            }


        return textList.joinToString("\n").tr().trim().toLabel()
    }


    fun update(){
        if(nation.name in newGameParameters.humanNations) pad(10f)
        else pad(0f)
        pack()
    }
}
package charbosses.cards.hermit;

import charbosses.cards.AbstractBossCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hermit.cards.Strike_Hermit;
import hermit.characters.hermit;

public class EnFreeStrikeHermit extends AbstractHermitBossCard {
    public static final String ID = "downfall_Charboss:Strike_Hermit";

    public EnFreeStrikeHermit() {
        super(ID, Strike_Hermit.NAME, "hermitResources/images/cards/card_strike.png", 1, Strike_Hermit.DESCRIPTION, CardType.ATTACK, hermit.Enums.COLOR_YELLOW, CardRarity.BASIC, CardTarget.ENEMY, AbstractMonster.Intent.ATTACK);
        this.baseDamage = 6;
        this.tags.add(CardTags.STRIKE);
        this.tags.add(CardTags.STARTER_STRIKE);
        setCostForTurn(0);
    }

    @Override
    public void use(final AbstractPlayer p, final AbstractMonster m) {
        this.addToBot(new DamageAction(p, new DamageInfo(m, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(3);
        }
    }

    @Override
    public void onSpecificTrigger() {
        setCostForTurn(cost);
        bossDarken();
    }

    @Override
    public AbstractCard makeCopy() {
        return new EnFreeStrikeHermit();
    }
}
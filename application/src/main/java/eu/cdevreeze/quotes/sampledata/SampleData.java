/*
 * Copyright 2024-2024 Chris de Vreeze
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cdevreeze.quotes.sampledata;

import com.google.common.collect.ImmutableList;
import eu.cdevreeze.quotes.model.QuoteData;

/**
 * Sample data, which can be used in unit tests to initialize a NonPersistentQuoteRepository.
 *
 * @author Chris de Vreeze
 */
public class SampleData {

    private SampleData() {
    }

    public static ImmutableList<QuoteData> allQuotes = ImmutableList.of(
            new QuoteData(
                    "If you can learn how to use your mind, anything is possible.",
                    "Wim Hof",
                    ImmutableList.of("inner strength")),
            new QuoteData(
                    "I'm not afraid of dying. I'm afraid not to have lived.",
                    "Wim Hof",
                    ImmutableList.of("inner strength")),
            new QuoteData(
                    """
                            I've come to understand that if you want to learn something badly enough,
                            you'll find a way to make it happen.
                            Having the will to search and succeed is very important""",
                    "Wim Hof",
                    ImmutableList.of("inner strength")),
            new QuoteData(
                    """
                            In nature, it is not only the physically weak but the mentally weak that get eaten.
                            Now we have created this modern society in which we have every comfort,
                            yet we are losing our ability to regulate our mood, our emotions.""",
                    "Wim Hof",
                    ImmutableList.of("inner strength")),
            new QuoteData(
                    """
                            Cold is a stressor, so if you are able to get into the cold and control your body's response to it,
                            you will be able to control stress.""",
                    "Wim Hof",
                    ImmutableList.of("inner strength")),
            new QuoteData(
                    """
                            Justifying conscription to promote the cause of liberty is one of the most bizarre notions ever conceived by man!
                            Forced servitude, with the risk of death and serious injury as a price to live free, makes no sense.""",
                    "Ron Paul",
                    ImmutableList.of("liberty")),
            new QuoteData(
                    """
                            When the federal government spends more each year than it collects in tax revenues,
                            it has three choices: It can raise taxes, print money, or borrow money.
                            While these actions may benefit politicians, all three options are bad for average Americans.""",
                    "Ron Paul",
                    ImmutableList.of("liberty")),
            new QuoteData(
                    """
                            Well, I don't think we should go to the moon.
                            I think we maybe should send some politicians up there.""",
                    "Ron Paul",
                    ImmutableList.of("politics")),
            new QuoteData(
                    """
                            I think a submarine is a very worthwhile weapon.
                            I believe we can defend ourselves with submarines and all our troops back at home.
                            This whole idea that we have to be in 130 countries and 900 bases...
                            is an old-fashioned idea.""",
                    "Ron Paul",
                    ImmutableList.of("liberty")),
            new QuoteData(
                    """
                            Of course I've already taken a very modest position on the monetary system,
                            I do take the position that we should just end the Fed.""",
                    "Ron Paul",
                    ImmutableList.of("liberty", "financial system")),
            new QuoteData(
                    """
                            Legitimate use of violence can only be that which is required in self-defense.""",
                    "Ron Paul",
                    ImmutableList.of("defense")),
            new QuoteData(
                    """
                            I am absolutely opposed to a national ID card.
                            This is a total contradiction of what a free society is all about.
                            The purpose of government is to protect the secrecy and the privacy of all individuals,
                            not the secrecy of government. We don't need a national ID card.""",
                    "Ron Paul",
                    ImmutableList.of("liberty")),
            new QuoteData(
                    """
                            Maybe we ought to consider a Golden Rule in foreign policy:
                            Don't do to other nations what we don't want happening to us.
                            We endlessly bomb these countries and then we wonder why they get upset with us?""",
                    "Ron Paul",
                    ImmutableList.of("liberty", "peace")),
            new QuoteData(
                    """
                            I am just absolutely convinced that the best formula for giving us peace and
                            preserving the American way of life is freedom, limited government,
                            and minding our own business overseas.""",
                    "Ron Paul",
                    ImmutableList.of("liberty", "peace")),
            new QuoteData(
                    """
                            Real patriotism is a willingness to challenge the government when it's wrong.""",
                    "Ron Paul",
                    ImmutableList.of("patriotism", "liberty")),
            new QuoteData(
                    """
                            Believe me, the intellectual revolution is going on,
                            and that has to come first before you see the political changes.
                            That's where I'm very optimistic.""",
                    "Ron Paul",
                    ImmutableList.of("politics")),
            new QuoteData(
                    """
                            War is never economically beneficial except for those in position to profit from war expenditures.""",
                    "Ron Paul",
                    ImmutableList.of("war", "profit")),
            new QuoteData(
                    """
                            There is only one kind of freedom and that's individual liberty.
                            Our lives come from our creator and our liberty comes from our creator.
                            It has nothing to do with government granting it.""",
                    "Ron Paul",
                    ImmutableList.of("liberty")),
            new QuoteData(
                    "Genius is patience",
                    "Isaac Newton",
                    ImmutableList.of("genius")),
            new QuoteData(
                    """
                            Atheism is so senseless.
                            When I look at the solar system,
                            I see the earth at the right distance from the sun to receive the proper amounts of heat and light.
                            This did not happen by chance.""",
                    "Isaac Newton",
                    ImmutableList.of("faith")),
            new QuoteData(
                    """
                            If I have seen further than others, it is by standing upon the shoulders of giants.""",
                    "Isaac Newton",
                    ImmutableList.of("achievements")),
            new QuoteData(
                    """
                            WAR is a racket.
                            It always has been.
                            It is possibly the oldest, easily the most profitable, surely the most vicious.
                            It is the only one international in scope.
                            It is the only one in which the profits are reckoned in dollars and the losses in lives.""",
                    "Smedley Butler",
                    ImmutableList.of("war")),
            new QuoteData(
                    """
                            I spent thirty-three years and four months in active military service as a member of this country's most agile military force,
                            the Marine Corps.
                            I served in all commissioned ranks from Second Lieutenant to Major-General.
                            And during that period, I spent most of my time being a high class muscle-man for Big Business, for Wall Street and for the Bankers.
                            In short, I was a racketeer, a gangster for capitalism.""",
                    "Smedley Butler",
                    ImmutableList.of("war", "conquest", "racket")),
            new QuoteData(
                    """
                            Only those who would be called upon to risk their lives for their country should have the privilege of voting
                            to determine whether the nation should go to war.""",
                    "Smedley Butler",
                    ImmutableList.of("war")),
            new QuoteData(
                    """
                            The illegal we do immediately; the unconstitutional takes a little longer.""",
                    "Henry Kissinger",
                    ImmutableList.of("corrupt government")),
            new QuoteData(
                    """
                            Military men are dumb, stupid animals to be used as pawns for foreign policy.""",
                    "Henry Kissinger",
                    ImmutableList.of("corrupt government", "hubris")),
            new QuoteData(
                    """
                            Every now and again the United States has to pick up a crappy little country and throw it against a wall
                            just to prove we are serious.""",
                    "Michael Ledeen",
                    ImmutableList.of("war", "hubris")),
            new QuoteData(
                    "We now have the technology to bring ET home.",
                    "Ben Rich",
                    ImmutableList.of("hidden knowledge")),
            new QuoteData(
                    "If you want to find the secrets of the universe, think in terms of energy, frequency and vibration.",
                    "Nikola Tesla",
                    ImmutableList.of("hidden knowledge")),
            new QuoteData(
                    """
                            The day science begins to study non-physical phenomena,
                            it will make more progress in one decade than in all the previous centuries of its existence.""",
                    "Nikola Tesla",
                    ImmutableList.of("hidden knowledge")
            )
    );
}

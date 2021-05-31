package ch.epfl.tchu.net;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

/**
 * Represents a serializer-deserializer (an object capable of serializing and deserializing values of a given type)
 *
 * @author Mehdi Bouguerra Ezzina (314857)
 * @author Noah Munz (310779)
 */
public interface Serde<E> {

    /**
     * Serialized an element to its String representation
     * @param element the element to be serialized
     * @return the String corresponding to the element
     * @throws IllegalArgumentException if the element is null
     */
    String serialize(E element);

    /**
     * Deserialized a String to the element it represent
     * @param message the message tto be deserialized
     * @return the element corresponding to the String
     * @throws IllegalArgumentException if the String is null
     */
    E deserialize(String message);

    /**
     * Gives a Serde corresponding to the two functions of serialization and deserialization passed in argument
     * @param serializer the serialization function
     * @param deserializer the deserialization function
     * @return a Serde corresponding to the two functions of serialization and deserialization passed in argument
     */
    static <T> Serde<T> of(Function<T, String> serializer, Function<String, T> deserializer) {
        return new Serde<T>() {
            @Override
            public String serialize(T element) {
                Preconditions.checkArgument(element != null);
                return serializer.apply(element);
            }

            @Override
            public T deserialize(String message) {
                Preconditions.checkArgument(message != null);
                return deserializer.apply(message);
            }
        };
    }

    /**
     * Method to get the composition of the serialization and deserialization functions.<br/>
     * Applies the intermediary function called f to the elements before serializing them.
     * Deserialization is proceeded before inverse of f.  <br/>
     * i.e. viewing the Types T,E as Sets: <br/>
     * Let f (from T -> E), g := inverse of f (from E -> T) <br/>
     * S:= the serialization function (from E -> String), D:= deserialization function (from String -> E) <br/>
     * we do: S(f(x)) to serialize element "x" as the String "y" <br/>
     * and: g(D(y)) to deserialize string "y".
     * we have that g(D(y)) = g(D(S(f(x))) = g(f(x)) = x
     * @param f intermediary function
     * @param fInverse mathematical inverse of f
     * @param <T> intermediary Type
     * @return new Serde that will performed the composition of given function and (de)serialization function
     */
    default <T> Serde<T> andThen(Function<T, E> f, Function<E, T> fInverse) {
        return of(element -> serialize(f.apply(element)),
                  message -> fInverse.apply(deserialize(message)));
    }

    /**
     * Gives a Serde corresponding to the list passed in argument
     * @param values List of a finite group of values
     * @return the Serde corresponding to the given list
     */
    static <T> Serde<T> oneOf(List<T> values) {
        return of(element -> String.valueOf(values.indexOf(element)),
                  message -> values.get(parseInt(message)));
    }

    /**
     * Gives a Serde capable of (de)serializing lists of values (de)serialized by the Serde passed in argument
     * @param serde the serde to which we refer to construct the new one
     * @param separator the separator used by the serde to split the different values
     * @param <T> the type parameter of the serde
     * @return a serde capable of (de)serializing lists of values (de)serialized by the given serde
     */
    static <T> Serde<List<T>> listOf(Serde<T> serde, String separator) {
        return of(elements -> elements.stream()
                                      .map(serde::serialize)
                                      .collect(Collectors.joining(separator)),

                  message -> message.equals("") ? List.of()
                                                : Arrays.stream(message.split(Pattern.quote(separator), -1))
                                                        .map(serde::deserialize)
                                                        .collect(Collectors.toList()));
    }

    /**
     * Gives a Serde capable of (de)serializing sorted multiset of values (de)serialized by the Serde passed in argument
     * @param serde the serde to which we refer to construct the new one
     * @param separator the separator used by the serde to split the different values
     * @param <T> the type parameter of the serde
     * @return a serde capable of (de)serializing sorted multiset of values (de)serialized by the given serde
     */
    static <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(Serde<T> serde, String separator) {
        return of(elements -> elements.stream()
                                      .map(serde::serialize)
                                      .collect(Collectors.joining(separator)),

                  message -> {
                      if (message.equals("")) return SortedBag.of();
                      else {
                          SortedBag.Builder<T> builder = new SortedBag.Builder<>();
                          Arrays.stream(message.split(Pattern.quote(separator), -1))
                                  .map(serde::deserialize)
                                  .forEach(builder::add);
                          return builder.build();
                      }
                  });
    }
}